package com.jotaro.register.zk.discovery.cache;

import com.alibaba.fastjson2.JSON;
import com.jotaro.register.zk.common.constants.ZKConstants;
import com.jotaro.register.zk.common.enums.ZKListenerEventTypeEnum;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

/**
 * @description: zk监听器
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/25 18:13
 */
@AllArgsConstructor
@Slf4j
public class ZKWatcher {

    private CuratorFramework zkClient;

    private CacheService cacheService;

    public void watch4Update(String path){
        CuratorCache curatorCache = CuratorCache.build(this.zkClient, path);
        curatorCache.listenable().addListener(new CuratorCacheListener() {
            @Override
            public void event(final Type type, final ChildData oldData, final ChildData data) {
                ZKListenerEventTypeEnum typeEnum = ZKListenerEventTypeEnum.getByName(type.name());
                switch (Objects.requireNonNull(typeEnum)){
                    case NODE_CREATED:
                        processNodeCreateEvent(data);
                        break;
                    case NODE_CHANGE:
                        processNodeUpdateEvent(oldData,data);
                        break;
                    case NODE_DELETED:
                        processNodeDeleteEvent(data);
                        break;
                    default:
                        log.error("unSupport zk event, type:{}", JSON.toJSONString(type));
                        break;

                }
            }
        });
        curatorCache.start();
    }


    private void processNodeCreateEvent(ChildData childData){
        String[] pathList = parsePath(childData);
        if (pathList.length > 2){
            String serviceName = pathList[1];
            String address = pathList[2];
            cacheService.addServiceToCache(serviceName,address);
        }
    }

    private String[] parsePath(ChildData childData){
        String path = new String(childData.getData());
        return path.split(ZKConstants.PATH_SPLIT_SYMBOL);
    }

    private void processNodeUpdateEvent(ChildData oldData, ChildData childData){
        if (Objects.nonNull(oldData.getData())){
            log.info("modify before data:{}", new String(oldData.getData()));
        }else {
            log.info("the first modify");
        }

        String[] oldPathList = parsePath(oldData);
        String[] newPathList = parsePath(childData);
        cacheService.replaceServiceAddress(oldPathList[1],oldPathList[2],newPathList[2]);
        log.info("serviceName:{} node modify from:{} to:{}",oldPathList[1], oldPathList[2],newPathList[2]);
    }

    private void processNodeDeleteEvent(ChildData childData){
        String[] pathList = parsePath(childData);
        if (pathList.length > 2){
            String serviceName = pathList[1];
            String address = pathList[2];
            cacheService.deleteAddress(serviceName,address);
        }
    }



}
