package com.jotaro.client.netty.impl;

import com.jotaro.serializer.JotaroDecoder;
import com.jotaro.serializer.JotaroEncoder;
import com.jotaro.serializer.impl.JsonSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @description: netty client初始化服务
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/23 00:44
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 初始化pipeline，实际pipeline执行数据加工的时候根据实现的接口是ChannelInboundHandler还是ChannelOutboundHandler调用具体的handler
     * 其中：实现ChannelOutboundHandler的是出站handle方式，实现ChannelInboundHandler的是入站的handler方式
     * @param ch            the {@link Channel} which was registered.
     * @throws Exception
     */
    @Override
    protected void initChannel(final SocketChannel ch) throws Exception {
        // 初始化，每个SocketChannel有一个独立的pipeline，用于定义数据的处理流程
        ChannelPipeline pipeline = ch.pipeline();
        // 消息格式 1. 长度 2. 消息体 --> 目的：解决tcp中的粘包问题
        pipeline.addLast(new LengthFieldBasedFrameDecoder(
                // 允许的最大数据帧的长度
                Integer.MAX_VALUE,
                // 消息头起始位置
                0,
                // 消息头所占字节数
                4,
                // 消息头后非消息体额外字节数
                0,
                // 解码后跳过字节数
                4));
        // 计算当前待发送消息的长度，写入到前4个字节
        pipeline.addLast(new LengthFieldPrepender(4));
        // 编码器，数据进行序列化
        pipeline.addLast(new JotaroEncoder(new JsonSerializer()));
        // 解码器, 传入ClassResolver，解析类名并加载对应的类
        pipeline.addLast(new JotaroDecoder());
        pipeline.addLast(new NettyClientHandler());

    }
}
