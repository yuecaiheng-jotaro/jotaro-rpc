package com.jotaro.client.netty.impl;

import com.jotaro.pojo.resp.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

/**
 * @description: 基于netty的handler处理器，绑定rpcResponse和channel的关系
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/22 21:25
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    /**
     * 读取服务端的数据
     * @param ctx           the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *                      belongs to
     * @param msg           the message to handle
     * @throws Exception
     */
    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final RpcResponse msg) throws Exception {
        // 将服务端返回的rpcResp绑定到当前的channel，方便后续通过channel获取到rpc的响应数据
        AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
        ctx.channel().attr(key).set(msg);
        // 关闭当前channel,短链接模式
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
