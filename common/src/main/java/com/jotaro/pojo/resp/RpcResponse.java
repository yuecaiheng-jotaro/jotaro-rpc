package com.jotaro.pojo.resp;

import lombok.Builder;
import lombok.Data;

/**
 * @description:
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/22 14:51
 */
@Data
@Builder
public class RpcResponse {

    private Integer code;

    private String message;

    private Object data;

    /**
     * success
     * @param data
     * @return
     */
    public static RpcResponse success(Object data){
        return RpcResponse.builder()
                .code(RespCodeEnum.SUCCESS.getCode())
                .data(data)
                .build();
    }

    /**
     * 默认fail体
     * @param message
     * @return
     */
    public static RpcResponse fail(String message){
        return RpcResponse.builder()
                .code(RespCodeEnum.UNKNOWN_ERROR.getCode())
                .message(message)
                .build();
    }

}
