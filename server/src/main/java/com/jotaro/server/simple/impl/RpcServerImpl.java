package com.jotaro.server.simple.impl;

import com.jotaro.server.common.ServerProvider;
import com.jotaro.server.RpcServer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: rpc服务端
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/22 17:07
 */
@Slf4j
public class RpcServerImpl implements RpcServer {

    private ServerProvider serverProvider;

    private ExecutorService providerExecutor = new ThreadPoolExecutor(
            10,
            10,
            0,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1024),
            new DiscardPolicy());


    @Override
    public void start(final int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            log.info("server starting!");
            while (true){
                Socket socket = serverSocket.accept();
                providerExecutor.submit(new WorkThread(socket,serverProvider));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }
}
