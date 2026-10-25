package com.jotaro.client.retry;

import com.jotaro.pojo.req.RpcRequest;
import com.jotaro.pojo.resp.RpcResponse;

/**
 * @description:
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/26 00:47
 */
public interface Retry {

    RpcResponse sendWithRetry(RpcRequest rpcRequest);


}
