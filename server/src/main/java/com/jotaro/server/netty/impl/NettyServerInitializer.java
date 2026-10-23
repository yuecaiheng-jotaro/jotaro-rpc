package com.jotaro.server.netty.impl;

import com.jotaro.server.common.ServerProvider;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.AllArgsConstructor;

/**
 * @description:
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/23 13:00
 */
@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private ServerProvider serverProvider;

    @Override
    protected void initChannel(final SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LengthFieldBasedFrameDecoder(
                Integer.MAX_VALUE,
                0,
                4,
                0,
                4
        ));
        pipeline.addLast(new LengthFieldPrepender(4));
        pipeline.addLast(new ObjectEncoder());
        pipeline.addLast(new ObjectDecoder(new ClassResolver() {
            @Override
            public Class<?> resolve(final String className) throws ClassNotFoundException {
                return Class.forName(className);
            }
        }));
        pipeline.addLast(new NettyRpcServerHandler(serverProvider));
    }
}
