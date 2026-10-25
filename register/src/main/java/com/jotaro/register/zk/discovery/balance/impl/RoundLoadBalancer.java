package com.jotaro.register.zk.discovery.balance.impl;

import com.jotaro.register.zk.discovery.balance.LoadBalancer;
import java.util.List;

/**
 * @description: 轮训负载均衡器
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/25 23:50
 */
public class RoundLoadBalancer implements LoadBalancer {

    private int choose = -1;

    @Override
    public String balance(final List<String> addressList) {
        choose++;
        choose = choose % addressList.size();
        return addressList.get(choose);
    }

    @Override
    public void addNode(final String node) {

    }

    @Override
    public void delNode(final String node) {

    }
}
