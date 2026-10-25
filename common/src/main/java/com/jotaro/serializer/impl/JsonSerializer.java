package com.jotaro.serializer.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.jotaro.common.MsgTypeEnum;
import com.jotaro.common.SerializeTypeEnum;
import com.jotaro.pojo.req.RpcRequest;
import com.jotaro.pojo.resp.RpcResponse;
import com.jotaro.serializer.Serializer;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: json序列化
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/25 15:00
 */
@Slf4j
public class JsonSerializer implements Serializer {

    @Override
    public byte[] serialize(final Object obj) {
        byte[] bytes = null;
        try {
            bytes = JSON.toJSONBytes(obj);
            return bytes;
        }catch (Exception e){
            log.error("json object serialize error",e);
            return null;
        }
    }

    @Override
    public Object deserialize(final byte[] bytes, final int msgType) {
        MsgTypeEnum type = MsgTypeEnum.getTypeById(msgType);
        if (Objects.isNull(type)){
            log.warn("not support msgType type:{}",type);
            return null;
        }
        try {
            switch (type){
                case REQ:
                    return parseReqObject(bytes);
                case RESP:
                    return parseRespObject(bytes);
                default:
                    return null;
            }
        }catch (Exception e){
            log.error("json object deserialize error",e);
            return null;
        }
    }

    @Override
    public Integer getSerializeType() {
        return SerializeTypeEnum.JSON.getType();
    }

    private Object parseReqObject(byte[] bytes){
        RpcRequest rpcRequest = JSON.parseObject(bytes,RpcRequest.class, JSONReader.Feature.SupportClassForName);
        Object[] objs = new Object[rpcRequest.getParamsType().length];
        for (int i = 0; i < objs.length; i++){
            Class<?> paramTtype = rpcRequest.getParamsType()[i];
            // isAssignableFrom 类型检查，用于判断class类型和指定数据是否兼容
            if (!paramTtype.isAssignableFrom(rpcRequest.getParams()[i].getClass())){
                objs[i] = JSON.to(paramTtype,rpcRequest.getParams()[i]);
            }else {
                objs[i] = rpcRequest.getParams()[i];
            }
        }
        rpcRequest.setParams(objs);
        return rpcRequest;
    }

    private Object parseRespObject(byte[] bytes){
        RpcResponse rpcResponse = JSON.parseObject(bytes,RpcResponse.class,JSONReader.Feature.SupportClassForName);
        log.info("rpcResp::{}", JSON.toJSONString(rpcResponse));
        Class<?> dataType = rpcResponse.getDataType();
        Object data = null;
        if (!dataType.isAssignableFrom(rpcResponse.getData().getClass())){
            data = JSON.to(dataType,rpcResponse.getData());
        }else {
            data = rpcResponse.getData();
        }
        rpcResponse.setData(data);
        return rpcResponse;
    }
}
