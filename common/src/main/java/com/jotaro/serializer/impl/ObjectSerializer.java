package com.jotaro.serializer.impl;

import com.jotaro.common.SerializeTypeEnum;
import com.jotaro.serializer.Serializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: java原生序列化
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/25 14:49
 */
@Slf4j
public class ObjectSerializer implements Serializer {

    @Override
    public byte[] serialize(final Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // 对象转为二进制流，写入bos
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();

            // 字节缓冲区内容转为字节数组
            bytes = bos.toByteArray();

            oos.close();
            bos.close();;
            return bytes;
        }catch (Exception e){
            log.error("java raw object serialize error",e);
            return null;
        }
    }

    @Override
    public Object deserialize(final byte[] bytes, final int msgType) {
        Object obj = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
            return obj;
        }catch (Exception e){
            log.error("java raw object deserialize error",e);
            return null;
        }
    }

    @Override
    public Integer getSerializeType() {
        return SerializeTypeEnum.JAVA_RAW.getType();
    }
}
