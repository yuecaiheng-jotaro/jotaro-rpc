package com.jotaro.register.zk.discovery;

import java.net.InetSocketAddress;

/**
 * @description: 注册中心接口
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/23 17:32
 */
public interface ServiceCenter {

    /**
     * 服务发现
     * @param serviceName
     * @return
     */
    InetSocketAddress serviceDiscovery(String serviceName);

}
