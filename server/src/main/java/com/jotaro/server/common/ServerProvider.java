package com.jotaro.server.common;

import com.google.common.collect.Maps;
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
     * 本地服务注册
     * @param service
     */
    public void provideServiceInterface(Object service){
        // 获取服务name
        String serverName = service.getClass().getName();
        // 获取实现类的实现的所有接口
        Class<?>[] interfaceNames = service.getClass().getInterfaces();

        for (Class clazz : interfaceNames){
            this.interfaceProvider.put(clazz.getName(),service);
        }
    }

    public Object getService(String interfaceName){
        return interfaceProvider.get(interfaceName);
    }

}
