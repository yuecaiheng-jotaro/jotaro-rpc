package com.jotaro.register.zk.discovery.balance.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jotaro.register.zk.discovery.balance.LoadBalancer;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @description: 一致性hash负载均衡
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/28 15:01
 */
@Slf4j
public class ConsistentHashLoadBalancer implements LoadBalancer {

    /**
     * 虚拟节点数量
     */
    private static final Integer VIRTUAL_NODES_NUM = 10;

    /**
     * 虚拟节点分隔符
     */
    private static final String VIRTUAL_SYMBOL = "#";

    /**
     * 真是address节点
     */
    private static final Set<String> REAL_NODES = Sets.newConcurrentHashSet();

    /**
     * 哈希值 to 虚拟节点
     */
    private static final TreeMap<Integer,String> HASH_2_VIRTUAL_NODES = Maps.newTreeMap();

    /**
     * 获取字符串哈希值
     * 默认default实现，支持构造传参
     */
    private static Function<String,Integer> getHash = str -> {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash ^ str.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        // Integer.MIN_VALUE 的绝对值仍为负，用高位清零保证非负
        return hash & Integer.MAX_VALUE;
    };

    /**
     * 重构哈希函数
     * @param hashFunc
     */
    public ConsistentHashLoadBalancer(final Function<String, Integer> hashFunc) {
        getHash = hashFunc;
    }


    @Override
    public String balance(final List<String> addressList) {
        String randomNumStr = String.valueOf(RandomUtil.randomInt());
        String address = getServer(getHash.apply(randomNumStr),addressList);
        if (StringUtils.isBlank(address)){
            log.error("[consistentHashLoadBalancer] can not get server, addressList:{}", addressList);
            return null;
        }
        return address;
    }

    private String getServer(Integer nodeHashNum, List<String> addressList){
        if (MapUtil.isEmpty(HASH_2_VIRTUAL_NODES)){
            init(addressList);
        }else {
            refresh(addressList);
        }
        TreeMap<Integer, String> subMap = (TreeMap<Integer, String>) HASH_2_VIRTUAL_NODES.tailMap(nodeHashNum);
        Integer key = subMap.isEmpty() ? HASH_2_VIRTUAL_NODES.firstKey() : subMap.firstKey();
        String selectedAddress = HASH_2_VIRTUAL_NODES.get(key);
        return StringUtils.isBlank(selectedAddress) ? null : selectedAddress.substring(0, selectedAddress.indexOf(VIRTUAL_SYMBOL));
    }

    @Override
    public synchronized void addNode(final String node) {
        if (REAL_NODES.contains(node)){
            return;
        }

        REAL_NODES.add(node);
        addVirtualNodes(node);
    }

    @Override
    public synchronized void delNode(final String node) {
        if (!REAL_NODES.contains(node)){
            return;
        }

        REAL_NODES.remove(node);
        this.delVirtualNodes(node);
    }

    private synchronized void init(List<String> addressList){
        addressList.forEach(address -> {
            ConsistentHashLoadBalancer.REAL_NODES.add(address);
            this.addVirtualNodes(address);
        });
    }

    /**
     * 添加虚拟节点
     * @param realAddress
     */
    private void addVirtualNodes(String realAddress){
        for (int i = 0;i < VIRTUAL_NODES_NUM; i++){
            String virtualNode = String.join(VIRTUAL_SYMBOL,realAddress,String.valueOf(i));
            HASH_2_VIRTUAL_NODES.put(getHash.apply(virtualNode),virtualNode);
        }
    }

    /**
     * 删除虚拟节点
     * @param realAddress
     */
    private void delVirtualNodes(String realAddress){
        for (int i = 0;i < VIRTUAL_NODES_NUM; i++){
            String virtualNode = String.join(VIRTUAL_SYMBOL,realAddress,String.valueOf(i));
            HASH_2_VIRTUAL_NODES.remove(getHash.apply(virtualNode));
        }
    }


    private synchronized void refresh(List<String> addressList){
        Set<String> newAddressSet = Sets.newHashSet(addressList);
        if (REAL_NODES.size() == newAddressSet.size()
                && REAL_NODES.containsAll(newAddressSet)){
            return;
        }

        Set<String> toRemoveNodes = Sets.difference(REAL_NODES,newAddressSet);
        Set<String> toAddNodes = Sets.difference(newAddressSet,REAL_NODES);

        toRemoveNodes.forEach(this::delNode);
        toAddNodes.forEach(this::addNode);

    }

}
