package com.jotaro.serializer;


/**
 * @description: 序列化器
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/25 14:45
 */
public interface Serializer {

    /**
     * 序列化
     * @param obj
     * @return
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     * @param bytes
     * @param msgType
     * @return
     */
    Object deserialize(byte[] bytes, int msgType);

    /**
     * 序列化方式
     * @return
     */
    Integer getSerializeType();

}
