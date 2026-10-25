package com.jotaro.register.zk.discovery.cache;

import java.util.List;

/**
 * @description: 缓存接口
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/25 17:52
 */
public interface CacheService {

    void addServiceToCache(String serviceName, String address);

    void replaceServiceAddress(String serviceName, String oldAddress, String newAddress);

    List<String> getServiceFromCache(String serviceName);

    void deleteAddress(String serviceName,String address);

}
