package com.jotaro.register.zk.discovery.balance;

import java.util.List;

/**
 * @description:
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/25 23:33
 */
public interface LoadBalancer {

    /**
     * 负载均衡选择addr
     * @param addressList
     * @return
     */
    String balance(List<String> addressList);

    /**
     * 添加节点
     * @param node
     */
    void addNode(String node);

    /**
     * 删除节点
     * @param node
     */
    void delNode(String node);

}
