package com.jotaro.register.zk.discovery.balance.impl;

import com.jotaro.register.zk.discovery.balance.LoadBalancer;
import java.util.List;
import java.util.Random;

/**
 * @description: 随机均衡器
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/25 23:57
 */
public class RandomLoadBalancer implements LoadBalancer {

    @Override
    public String balance(final List<String> addressList) {
        Random random = new Random();
        int choose = random.nextInt(addressList.size());
        return addressList.get(choose);
    }

    @Override
    public void addNode(final String node) {

    }

    @Override
    public void delNode(final String node) {

    }
}
