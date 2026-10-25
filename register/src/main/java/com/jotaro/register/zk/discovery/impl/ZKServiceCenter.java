package com.jotaro.register.zk.discovery.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.jotaro.register.zk.ZKClientFactory;
import com.jotaro.register.zk.common.constants.ZKConstants;
import com.jotaro.register.zk.common.utils.ZKUtils;
import com.jotaro.register.zk.discovery.ServiceCenter;
import com.jotaro.register.zk.discovery.balance.LoadBalancer;
import com.jotaro.register.zk.discovery.balance.impl.RoundLoadBalancer;
import com.jotaro.register.zk.discovery.cache.CacheService;
import com.jotaro.register.zk.discovery.cache.ZKWatcher;
import com.jotaro.register.zk.discovery.cache.impl.RawLocalCacheService;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

/**
 * @description: zk注册中心
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/23 17:33
 */
@Slf4j
public class ZKServiceCenter implements ServiceCenter {

    // zk客户端
    private CuratorFramework zkClient;

    // 注册中心缓存
    private CacheService cacheService;

    // 负载均衡器
    private LoadBalancer loadBalancer;

    // 初始化zk的客户端连接
    public ZKServiceCenter() {
        // 初始化zk链接
        this.zkClient = ZKClientFactory.buildZkClient();
        this.zkClient.start();
        try {
            if (!this.zkClient.blockUntilConnected(15, TimeUnit.SECONDS)) {
                throw new IllegalStateException("zk discovery client connect timeout");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("zk discovery client connect interrupted", e);
        }
        log.info("discovery zkClient connect success!");

        // 初始化监听器和缓存
        this.cacheService = new RawLocalCacheService();
        ZKWatcher watcher = new ZKWatcher(this.zkClient,this.cacheService);
        watcher.watch4Update(ZKConstants.PATH_SPLIT_SYMBOL);

        // 初始化负载均衡器
        this.loadBalancer = new RoundLoadBalancer();
    }

    @Override
    public InetSocketAddress serviceDiscovery(final String serviceName) {
        try {
            // 获取服务提供的节点字符串ip:port（namespace 下路径与注册侧保持一致）
            List<String> serviceNodesStr = cacheService.getServiceFromCache(serviceName);
            if (CollectionUtil.isEmpty(serviceNodesStr)){
                serviceNodesStr = this.zkClient.getChildren().forPath(ZKUtils.getZkPathByServiceName(serviceName));
            }
            if (CollectionUtil.isEmpty(serviceNodesStr)) {
                log.error("ZKServiceCenter.serviceDiscovery empty, serviceName:{}", serviceName);
                return null;
            }
            return parseAddress(this.loadBalancer.balance(serviceNodesStr));

        }catch (Exception e){
            log.error("ZKServiceCenter.serviceDiscovery, serviceName:{}",serviceName,e);
            return null;
        }
    }

    private  InetSocketAddress parseAddress(String address){
        String[] ipPortStr = address.split(":");
        return new InetSocketAddress(ipPortStr[0],Integer.parseInt(ipPortStr[1]));
    }
}
