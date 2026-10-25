package com.jotaro.client.retry;

import com.jotaro.client.netty.RpcClient;

/**
 * @description:
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/26 00:48
 */
public abstract class RetryHandler implements Retry{

    protected RpcClient rpcClient;

}
