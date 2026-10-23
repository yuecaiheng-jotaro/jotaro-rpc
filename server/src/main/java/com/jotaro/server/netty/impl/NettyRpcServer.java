package com.jotaro.server.netty.impl;

import com.jotaro.pojo.resp.RpcResponse;
import com.jotaro.server.RpcServer;
import com.jotaro.server.common.ServerProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: netty服务端主线程
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/23 13:05
 */
@Slf4j
@AllArgsConstructor
public class NettyRpcServer implements RpcServer {

    private ServerProvider serverProvider;

    @Override
    public void start(final int port) {
        // netty 服务线程组boss负责建立连接， work负责具体的请求
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        System.out.println("netty server starting!");
        try {
            // 启动netty服务器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 初始化
            serverBootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    //NettyClientInitializer这里 配置netty对消息的处理机制
                    .childHandler(new NettyServerInitializer(serverProvider));
            // 同步堵塞
            ChannelFuture channelFuture=serverBootstrap.bind(port).sync();
            // 死循环监听，服务启动后阻塞到这里，等待channel完全close掉再往下执行
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            log.error("netty server start fail!!!",e);
        }
    }

    @Override
    public void stop() {

    }
}
