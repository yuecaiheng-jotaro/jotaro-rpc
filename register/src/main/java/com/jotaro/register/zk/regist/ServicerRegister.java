package com.jotaro.register.zk.regist;

import java.net.InetSocketAddress;

/**
 * @description: 注册中心
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/24 21:12
 */
public interface ServicerRegister {

    /**
     * 服务注册
     * @param serviceName
     * @param address
     * @return
     */
    boolean serviceRegister(String serviceName, InetSocketAddress address);

}
