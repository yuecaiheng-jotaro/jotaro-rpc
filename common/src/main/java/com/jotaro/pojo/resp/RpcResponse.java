package com.jotaro.pojo.resp;

import com.jotaro.common.RespCodeEnum;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

/**
 * @description:
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/22 14:51
 */
@Data
@Builder
public class RpcResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private String message;

    private Object data;

    private Class<?> dataType;

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
     * success
     * @param data
     * @return
     */
    public static RpcResponse success(Object data, Class<?> dataType){
        return RpcResponse.builder()
                .code(RespCodeEnum.SUCCESS.getCode())
                .data(data)
                .dataType(dataType)
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
