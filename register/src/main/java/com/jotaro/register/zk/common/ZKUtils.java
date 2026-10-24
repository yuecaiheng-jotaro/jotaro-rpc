package com.jotaro.register.zk.common;

import java.net.InetSocketAddress;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.lf5.util.StreamUtils;

/**
 * @description:
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/24 21:20
 */
public class ZKUtils {

    /**
     * 根据服务名获取zk 路径
     * @param serviceName
     * @return
     */
    static public String getZkPathByServiceName(String serviceName){
        if (StringUtils.isNotBlank(serviceName)){
            return ZKConstants.PATH_SPLIT_SYMBOL + serviceName;
        }
        return null;
    }

    /**
     * inet 转 ip:pory字符串
     * @param address
     * @return
     */
    static public String getServiceInetAddress(InetSocketAddress address){
        if (Objects.nonNull(address)){
            return String.join(ZKConstants.SPLIT_HOST_PORT_SYMBOL, address.getHostName(),String.valueOf(address.getPort()));
        }
        return null;
    }

}
