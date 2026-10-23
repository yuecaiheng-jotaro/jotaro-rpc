package com.jotaro.pojo.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/22 14:54
 */
@Getter
@AllArgsConstructor
public enum RespCodeEnum {

    UNKNOWN_ERROR(-1, "未知错误"),

    SUCCESS(0, "成功")

    ;

    private final Integer code;

    private final String msg;
}
