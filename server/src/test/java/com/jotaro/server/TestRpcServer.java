package com.jotaro.server;

import com.jotaro.api.UserService;
import com.jotaro.api.UserServiceImpl;
import com.jotaro.server.common.ServerProvider;
import com.jotaro.server.netty.impl.NettyRpcServer;
import org.junit.Test;


/**
 * @description:
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/22 18:47
 */
public class TestRpcServer {

    @Test
    public void testServer(){
        UserService userService = new UserServiceImpl();
        ServerProvider serverProvider = new ServerProvider();
        serverProvider.provideServiceInterface(userService);

        RpcServer rpcServer = new NettyRpcServer(serverProvider);
        rpcServer.start(9999);

    }

}
