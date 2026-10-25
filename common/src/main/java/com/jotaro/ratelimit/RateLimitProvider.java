package com.jotaro.ratelimit;

import com.google.common.collect.Maps;
import com.jotaro.ratelimit.impl.TokenBucketRateLimitImpl;
import java.util.Map;

/**
 * @description:
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/26 01:39
 */
public class RateLimitProvider {

    private static final String SERVICE_METHOD_SPLIT = "#";

    private Map<String, RateLimiter> rateLimiterMap = Maps.newHashMap();

    public RateLimiter getRateLimit(String serviceName,String methodName){
        String fullPath = String.join(SERVICE_METHOD_SPLIT,serviceName,methodName);
        if (!rateLimiterMap.containsKey(fullPath)){
            RateLimiter rateLimiter = new TokenBucketRateLimitImpl(100,10);
            rateLimiterMap.put(fullPath,rateLimiter);
            return rateLimiter;
        }
        return rateLimiterMap.get(fullPath);
    }

}
