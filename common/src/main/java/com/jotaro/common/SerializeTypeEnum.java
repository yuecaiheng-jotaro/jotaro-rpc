package com.jotaro.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 支持的序列化方式
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/25 14:58
 */
@Getter
@AllArgsConstructor
public enum SerializeTypeEnum {
    /**
     * java原生序列化方式
     */
    JAVA_RAW(0),

    /**
     * json序列化
     */
    JSON(1),
    ;
    private final Integer type;
}
