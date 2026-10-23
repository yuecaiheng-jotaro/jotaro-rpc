package com.jotaro.server.simple.impl;

import com.alibaba.fastjson2.JSON;
import com.jotaro.pojo.req.RpcRequest;
import com.jotaro.pojo.resp.RpcResponse;
import com.jotaro.server.common.ServerProvider;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @description:
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/22 16:53
 */
@Slf4j
@AllArgsConstructor
public class WorkThread implements Runnable {

    private Socket socket;

    private ServerProvider serverProvider;

    @Override
    public void run() {
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            RpcRequest rpcRequest = (RpcRequest) ois.readObject();
            RpcResponse rpcResponse = getResponse(rpcRequest);

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(rpcResponse);
            oos.flush();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private RpcResponse getResponse(RpcRequest rpcRequest){
        String interfaceName = rpcRequest.getInterfaceName();
        Object service = serverProvider.getService(interfaceName);
        Method method = null;
        try{
            method = service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamsType());
            Object invoke = method.invoke(service, rpcRequest.getParams());
            return RpcResponse.success(invoke);
        }catch (Exception e){
            log.error("WorkThread.getResponse error, rpcRequest:{}",JSON.toJSONString(rpcRequest),e);
            return RpcResponse.fail("服务端异常");
        }
    }
}
