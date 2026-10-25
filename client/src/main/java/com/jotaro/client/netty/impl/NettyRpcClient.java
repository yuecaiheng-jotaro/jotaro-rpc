package com.jotaro.client.netty.impl;

import com.jotaro.pojo.req.RpcRequest;
import com.jotaro.pojo.resp.RpcResponse;
import com.jotaro.register.zk.discovery.ServiceCenter;
import com.jotaro.register.zk.discovery.impl.ZKServiceCenter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import com.jotaro.client.netty.RpcClient;

/**
 * @description: netty rpc客户端
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/23 01:32
 */
@Slf4j
public class NettyRpcClient implements RpcClient {

    private ServiceCenter serviceCenter;

    private static final Bootstrap bootstrap;

    private static final EventLoopGroup eventLoopGroup;

    static {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());
    }

    public NettyRpcClient() {
        this.serviceCenter = new ZKServiceCenter();
    }

    @Override
    public RpcResponse sendRequest(final RpcRequest rpcRequest) {
        try {
            // 创建channelFuture对象，代表操作事件，sync阻塞到connect完成
            ChannelFuture channelFuture = bootstrap
                    .connect(this.serviceCenter.serviceDiscovery(rpcRequest.getInterfaceName()))
                    .sync();
            // 获取channel
            Channel channel = channelFuture.channel();
            // 发送数据；写出失败时主动关连接，避免 closeFuture 永久堵塞
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    log.error("netty write failed", future.cause());
                    channel.close();
                }
            });
            // 堵塞获取结果
            channel.closeFuture().sync();
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            RpcResponse rpcResponse = channel.attr(key).get();
            return rpcResponse;
        }catch (Exception e){
            log.error("nettyClient send error",e);
            return RpcResponse.fail("发送异常");
        }
    }

    @Override
    public RpcResponse sendResuestRetry(final RpcRequest rpcRequest) {
        return null;
    }
}
