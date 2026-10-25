package com.jotaro.register.zk.discovery.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.jotaro.register.zk.ZKClientFactory;
import com.jotaro.register.zk.common.ZKUtils;
import com.jotaro.register.zk.discovery.ServiceCenter;
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

    // 初始化zk的客户端连接
    public ZKServiceCenter() {
        this.zkClient = ZKClientFactory.getZkClient();
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
    }

    @Override
    public InetSocketAddress serviceDiscovery(final String serviceName) {
        try {
            // 获取服务提供的节点字符串ip:port（namespace 下路径与注册侧保持一致）
            List<String> serviceNodesStr = this.zkClient.getChildren()
                    .forPath(ZKUtils.getZkPathByServiceName(serviceName));
            if (CollectionUtil.isEmpty(serviceNodesStr)) {
                log.error("ZKServiceCenter.serviceDiscovery empty, serviceName:{}", serviceName);
                return null;
            }
            return parseAddress(serviceNodesStr.get(0));

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
