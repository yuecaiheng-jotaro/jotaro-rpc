package com.jotaro.common;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 消息类型枚举
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/25 15:04
 */
@Getter
@AllArgsConstructor
public enum MsgTypeEnum {

    REQ(0),

    RESP(1)

    ;

    private final int code;

    public static MsgTypeEnum getTypeById(Integer code){
        for (MsgTypeEnum type : MsgTypeEnum.values()){
            if (Objects.equals(type.getCode(),code)){
                return type;
            }
        }
        return null;
    }

}
