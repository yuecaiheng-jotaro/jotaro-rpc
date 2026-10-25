package com.jotaro.register.zk.discovery.cache.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jotaro.register.zk.discovery.cache.CacheService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 原生本地缓存实现
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/25 17:37
 */
@Slf4j
public class RawLocalCacheService implements CacheService {

    private static final Map<String, List<String>> cache = Collections.synchronizedMap(Maps.newHashMap());

    @Override
    public void addServiceToCache(final String serviceName, final String address) {
        if (cache.containsKey(serviceName)){
            cache.get(serviceName).add(address);
        }else {
            cache.put(serviceName, Collections.synchronizedList(Lists.newArrayList(address)));
        }
    }

    @Override
    public void replaceServiceAddress(final String serviceName, final String oldAddress,
            final String newAddress) {
        if (!cache.containsKey(serviceName)){
            log.error("cache not contain service, serviceName:{}",serviceName);
            return;
        }
        cache.get(serviceName).remove(oldAddress);
        cache.get(serviceName).add(newAddress);
    }

    @Override
    public List<String> getServiceFromCache(final String serviceName) {
        return cache.get(serviceName);
    }

    @Override
    public void deleteAddress(final String serviceName, final String address) {
        if (!cache.containsKey(serviceName)){
            log.error("cache not contain service, serviceName:{}",serviceName);
        }
        cache.get(serviceName).remove(address);
    }
}
