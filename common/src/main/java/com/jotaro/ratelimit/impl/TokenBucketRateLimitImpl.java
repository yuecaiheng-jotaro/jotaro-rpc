package com.jotaro.ratelimit.impl;

import com.jotaro.ratelimit.RateLimiter;

/**
 * @description: 令牌桶限流器
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/26 01:30
 */
public class TokenBucketRateLimitImpl implements RateLimiter {

    private static Integer RATE;

    private static Integer CAPACITY;

    private volatile Integer curCapacity;

    private volatile Long lastTimeStamp = System.currentTimeMillis();

    public TokenBucketRateLimitImpl(int rate, int capacity) {
        TokenBucketRateLimitImpl.RATE = rate;
        TokenBucketRateLimitImpl.CAPACITY = capacity;
        this.curCapacity = capacity;
    }

    @Override
    public boolean getToken() {
        if (curCapacity > 0){
            curCapacity--;
            return true;
        }

        Long current = System.currentTimeMillis();

        if (current - lastTimeStamp >= RATE){
            if ((current - lastTimeStamp) / RATE >= 2){
                curCapacity += (int)((current - lastTimeStamp) / RATE) - 1;
            }
            if (curCapacity > CAPACITY){
                curCapacity = CAPACITY;
            }
            lastTimeStamp = current;
            return true;
        }
        return false;
    }
}
