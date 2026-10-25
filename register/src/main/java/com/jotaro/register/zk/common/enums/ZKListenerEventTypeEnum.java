package com.jotaro.register.zk.common.enums;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/25 22:57
 */
@Getter
public enum ZKListenerEventTypeEnum {

    /**
     * 新建节点
     */
    NODE_CREATED,

    /**
     * 节点变更
     */
    NODE_CHANGE,

    /**
     * 删除节点
     */
    NODE_DELETED

    ;

    public static ZKListenerEventTypeEnum getByName(String name){
        for (ZKListenerEventTypeEnum typeEnum : ZKListenerEventTypeEnum.values()){
            if (Objects.equals(typeEnum.name(),name)){
                return typeEnum;
            }
        }
        return null;
    }


}
