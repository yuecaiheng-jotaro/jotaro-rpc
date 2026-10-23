package com.jotaro.client.netty;

import com.jotaro.client.RpcClient;
import com.jotaro.pojo.req.RpcRequest;
import com.jotaro.pojo.resp.RpcResponse;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import lombok.AllArgsConstructor;
import com.jotaro.client.netty.impl.NettyRpcClient;


/**
 * @description: IO客户端代理服务
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/22 15:07
 */
@AllArgsConstructor
public class NettyClientProxy implements InvocationHandler {

    private String host;

    private Integer port;

    private RpcClient ioClient;

    public NettyClientProxy(String host, Integer port) {
        this.host = host;
        this.port = port;
        this.ioClient = new NettyRpcClient(host,port);
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {

        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes())
                .build();

        RpcResponse rpcResponse = this.ioClient.sendRequest(rpcRequest);
        if (rpcResponse == null) {
            throw new IllegalStateException("rpc response is null, request=" + method.getName());
        }
        return rpcResponse.getData();
    }

    public <T>T getProxy(Class<T> clazz){
        Object obj = Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},this);
        return (T)obj;
    }
}
