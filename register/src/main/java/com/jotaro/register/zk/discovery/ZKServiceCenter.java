package com.jotaro.register.zk.discovery;

import com.jotaro.register.zk.ZKClientFactory;
import java.net.InetSocketAddress;
import java.util.List;
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
        log.info("discovery zkClient connect success!");
    }

    @Override
    public InetSocketAddress serviceDiscovery(final String serviceName) {
        try {
            // 获取服务提供的节点字符串ip:port
            List<String> serviceNodesStr = this.zkClient.getChildren().forPath("/" + serviceName);
            InetSocketAddress defaultInetAddress = parseAddress(serviceNodesStr.get(0));
            return defaultInetAddress;

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
