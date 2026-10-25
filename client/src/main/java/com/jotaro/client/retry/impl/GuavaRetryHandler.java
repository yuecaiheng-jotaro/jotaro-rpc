package com.jotaro.client.retry.impl;

import cn.hutool.core.util.ObjectUtil;
import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.RetryListener;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.jotaro.client.retry.RetryHandler;
import com.jotaro.common.RespCodeEnum;
import com.jotaro.pojo.req.RpcRequest;
import com.jotaro.pojo.resp.RpcResponse;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: guava重试handler
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/26 00:47
 */
@Slf4j
@AllArgsConstructor
public class GuavaRetryHandler extends RetryHandler {

    @Override
    public RpcResponse sendWithRetry(final RpcRequest rpcRequest) {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                // 异常重试
                .retryIfException()
                // 条件重试
                .retryIfResult(rpcResponse -> Objects.isNull(rpcResponse) || ObjectUtil.notEqual(RespCodeEnum.SUCCESS,rpcResponse.getCode()))
                // 每次重试等待2秒
                .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
                // 最多3次
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                // 重试监听器
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(final Attempt<V> attempt) {
                        log.warn("[RetryListener]retry {} times",attempt.getAttemptNumber());
                    }
                })
                .build();
        try {
            return retryer.call(() -> this.rpcClient.sendRequest(rpcRequest));
        }catch (Exception e){
            log.error("[RetryListener]retry call error",e);
            return RpcResponse.fail("重试异常");
        }
    }


}
