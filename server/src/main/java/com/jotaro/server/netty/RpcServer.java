package com.jotaro.server.netty;

/**
 * @description: rpc服务端
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/22 15:59
 */
public interface RpcServer {

    /**
     * 开启服务监听
     * @param port
     */
    void start(int port);

    /**
     * 停止服务
     */
    void stop();

}
