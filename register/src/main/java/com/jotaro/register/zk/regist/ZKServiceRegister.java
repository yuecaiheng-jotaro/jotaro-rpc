package com.jotaro.register.zk.regist;

import com.alibaba.fastjson2.JSON;
import com.jotaro.register.zk.ZKClientFactory;
import com.jotaro.register.zk.common.ZKConstants;
import com.jotaro.register.zk.common.ZKUtils;
import java.net.InetSocketAddress;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

/**
 * @description: zk注册中心实现
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/24 21:11
 */
@Slf4j
public class ZKServiceRegister implements ServicerRegister{

    private CuratorFramework zkClient;

    public ZKServiceRegister() {
        this.zkClient = ZKClientFactory.getZkClient();
        this.zkClient.start();
        log.info("register zkClient connect success!");
    }

    @Override
    public boolean serviceRegister(final String serviceName, final InetSocketAddress address) {
        try {
            // 如果未曾接入过该服务，第一次接入时创建永久节点，服务下线时不删除服务路径，只删除node
            if (Objects.nonNull(this.zkClient.checkExists().forPath(ZKUtils.getZkPathByServiceName(serviceName)))){
                this.zkClient.create()
                        // 确保父路径存在，如果父路径不存在则会先创建父路径
                        .creatingParentsIfNeeded()
                        // 服务为永久节点
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(ZKUtils.getZkPathByServiceName(serviceName));
            }

            // 将当前idc的地址注册到服务路径中
            this.zkClient.create()
                    .creatingParentsIfNeeded()
                    // idc的host为临时节点
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(String.join(ZKConstants.PATH_SPLIT_SYMBOL,
                            ZKUtils.getZkPathByServiceName(serviceName),
                            ZKUtils.getServiceInetAddress(address)));
            return true;

        }catch (Exception e){
            log.error("service register error, serviceName:{}, address:{}", serviceName, JSON.toJSONString(address),e);
            return false;
        }
    }
}
