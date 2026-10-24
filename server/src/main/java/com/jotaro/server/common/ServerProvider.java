package com.jotaro.server.common;

import com.google.common.collect.Maps;
import com.jotaro.conn.ConnAddress;
import com.jotaro.register.zk.regist.ServicerRegister;
import com.jotaro.register.zk.regist.ZKServiceRegister;
import java.net.InetSocketAddress;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 服务提供引擎
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/22 16:01
 */
@Slf4j
public class ServerProvider {

    /**
     * 接口name to 接口实现类
     */
    private Map<String,Object> interfaceProvider = Maps.newHashMap();

    /**
     * 服务注册器
     */
    private ServicerRegister servicerRegister;

    /**
     * 保存ip port
     */
    private ConnAddress connAddress;


    public ServerProvider(String host,int port) {
        this.connAddress = ConnAddress.builder()
                .host(host)
                .port(port)
                .build();
        this.servicerRegister = new ZKServiceRegister();
    }

    /**
     * 本地服务注册
     * @param service
     */
    public void provideServiceInterface(Object service){
        // 获取实现类的实现的所有接口
        Class<?>[] interfaceNames = service.getClass().getInterfaces();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(this.connAddress.getHost(),this.connAddress.getPort());
        for (Class clazz : interfaceNames){
            this.interfaceProvider.put(clazz.getName(),service);
            this.servicerRegister.serviceRegister(clazz.getName(), inetSocketAddress);
        }
    }

    public Object getService(String interfaceName){
        return interfaceProvider.get(interfaceName);
    }

}
