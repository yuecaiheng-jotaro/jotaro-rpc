package com.jotaro.client.simple;

import com.alibaba.fastjson2.JSON;

import com.jotaro.pojo.req.RpcRequest;
import com.jotaro.pojo.resp.RpcResponse;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import lombok.extern.slf4j.Slf4j;


/**
 * @description: IO通信客户端
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/22 14:47
 */
@Slf4j
public class IOClient {

    public static RpcResponse sendRequest(String host, int port, RpcRequest rpcRequest){
        try{
            Socket socket = new Socket(host, port);

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(rpcRequest);
            oos.flush();

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            RpcResponse rpcResponse = (RpcResponse) ois.readObject();
            return rpcResponse;

        }catch (Exception e){
            log.error("IOClient.sendRequest error, host:{}, port:{}, rpcRequest:{}", host, port, JSON.toJSONString(rpcRequest),e);
            return RpcResponse.fail("rpc异常");
        }
    }

}
