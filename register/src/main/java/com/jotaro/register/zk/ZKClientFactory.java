package com.jotaro.register.zk;

import com.jotaro.register.zk.common.constants.ZKConstants;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    private static final String ZK_CONFIG_FILE = "zk.propertis";

    private static final String ZK_ADDRESS_KEY = "jrpc.zk.address";

    private static final String DEFAULT_ZK_ADDRESS = "localhost:2181";

    static public CuratorFramework buildZkClient(){
        String connectString = loadZkAddress();
        log.info("zk connectString:{}", connectString);
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

    /**
     * 加载zk配置文件
     * @return
     */
    private static String loadZkAddress() {
        Properties properties = new Properties();
        try (InputStream inputStream = ZKClientFactory.class.getClassLoader()
                .getResourceAsStream(ZK_CONFIG_FILE)) {
            if (inputStream == null) {
                log.warn("zk config {} not found on classpath, fallback to {}", ZK_CONFIG_FILE, DEFAULT_ZK_ADDRESS);
                return DEFAULT_ZK_ADDRESS;
            }
            properties.load(inputStream);
            String address = properties.getProperty(ZK_ADDRESS_KEY);
            if (StringUtils.isBlank(address)) {
                log.warn("zk address key {} is blank, fallback to {}", ZK_ADDRESS_KEY, DEFAULT_ZK_ADDRESS);
                return DEFAULT_ZK_ADDRESS;
            }
            return address.trim();
        } catch (IOException e) {
            log.error("load zk config {} failed, fallback to {}", ZK_CONFIG_FILE, DEFAULT_ZK_ADDRESS, e);
            return DEFAULT_ZK_ADDRESS;
        }
    }

}
