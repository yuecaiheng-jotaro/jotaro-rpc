package com.jotaro.pojo.req;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

/**
 * @description: rpc请求体
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/22 14:49
 */
@Data
@Builder
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 接口名称
     */
    private String interfaceName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 参数列表
     */
    private Object[] params;

    /**
     * 参数类型列表
     */
    private Class<?>[] paramsType;

}
