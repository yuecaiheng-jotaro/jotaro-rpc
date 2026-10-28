package com.jotaro.ratelimit;

/**
 * @description: 服务限流接口
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/26 01:28
 */
public interface RateLimiter {

    boolean getToken();

}
