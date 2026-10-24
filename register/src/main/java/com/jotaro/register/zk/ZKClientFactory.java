package com.jotaro.register.zk;

import com.jotaro.register.zk.common.ZKConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @description:
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/24 21:15
 */
@Slf4j
public class ZKClientFactory {

    static public CuratorFramework getZkClient(){
        // 可通过 -Djrpc.zk.connectString=host:port 覆盖，默认本机 ZK
        String connectString = System.getProperty("jrpc.zk.connectString", "10.211.55.6:2181");
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                // 与zk建立连接
                .connectString(connectString)
                // 客户端会话超时时间，seesionTimeoutMs与zoo.cfg的ticketTime有关，40s没有心跳认为失联
                .sessionTimeoutMs(40000)
                // 连接超时，避免远端不可达时长时间卡死
                .connectionTimeoutMs(15000)
                // 指定时间重试,指数回退，base 回退时间1000ms
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace(ZKConstants.ROOT_PATH)
                .build();
        return zkClient;
    }

}
