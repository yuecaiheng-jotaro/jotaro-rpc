package com.jotaro.common;

import com.jotaro.serializer.Serializer;
import com.jotaro.serializer.impl.JsonSerializer;
import com.jotaro.serializer.impl.ObjectSerializer;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/25 16:58
 */
@Getter
@AllArgsConstructor
public enum Type2SSerializerEnum {

    JAVA_RAW_SERIALIZER(0, ObjectSerializer.class),

    JSON_SERIALIZER(1, JsonSerializer.class);


    private final int type;

    private final Class<?> serializer;

    public static Serializer getSerializerByType(int type) throws InstantiationException, IllegalAccessException {
        for (Type2SSerializerEnum type2SSerializerEnum : Type2SSerializerEnum.values()){
            if (Objects.equals(type2SSerializerEnum.getType(),type)){
                return (Serializer) type2SSerializerEnum.getSerializer().newInstance();
            }
        }
        return null;
    }
}
