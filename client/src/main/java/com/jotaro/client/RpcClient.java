package com.jotaro.client;

import com.jotaro.pojo.req.RpcRequest;
import com.jotaro.pojo.resp.RpcResponse;

/**
 * @description:
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/22 21:14
 */
public interface RpcClient {

    RpcResponse sendRequest(RpcRequest rpcRequest);

}
