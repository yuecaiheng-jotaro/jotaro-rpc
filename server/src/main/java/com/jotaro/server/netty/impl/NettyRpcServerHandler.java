package com.jotaro.server.netty.impl;

import com.alibaba.fastjson2.JSON;
import com.jotaro.pojo.req.RpcRequest;
import com.jotaro.pojo.resp.RpcResponse;
import com.jotaro.ratelimit.RateLimiter;
import com.jotaro.server.common.ServerProvider;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.lang.reflect.Method;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @description:
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/23 01:59
 */
@Slf4j
@AllArgsConstructor
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private ServerProvider serverProvider;

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final RpcRequest rpcRequest) throws Exception {
        RpcResponse rpcResponse = getResponse(rpcRequest);
        // writeAndFlush 是异步的，必须等写出完成后再关连接，否则客户端可能永远收不到响应
        ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private RpcResponse getResponse(RpcRequest rpcRequest){
        log.info("server process request:{}",JSON.toJSONString(rpcRequest));
        String interfaceName = rpcRequest.getInterfaceName();
        Object service = serverProvider.getService(interfaceName);
        if (service == null) {
            log.error("service not found, interfaceName:{}", interfaceName);
            return RpcResponse.fail("服务未找到: " + interfaceName);
        }
        Method method = null;
        try{
            method = service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamsType());
            RateLimiter rateLimiter = this.serverProvider.getRateLimiter().getRateLimit(interfaceName,method.getName());
            if (!rateLimiter.getToken()){
                log.error("ratelimit sevicenName:{}, methodName:{}",interfaceName,method.getName());
                return RpcResponse.fail("服务限流");
            }
            Object invoke = method.invoke(service, rpcRequest.getParams());
            return RpcResponse.success(invoke, method.getReturnType());
        }catch (Exception e){
            log.error("WorkThread.getResponse error, rpcRequest:{}", JSON.toJSONString(rpcRequest),e);
            return RpcResponse.fail("服务端异常");
        }
    }
}
