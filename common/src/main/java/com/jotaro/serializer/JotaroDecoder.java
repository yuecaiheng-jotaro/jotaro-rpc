package com.jotaro.serializer;

import com.jotaro.common.MsgTypeEnum;
import com.jotaro.common.Type2SSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 自定义解码器
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/25 16:33
 */
@Slf4j
public class JotaroDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        short msgTypeCode = in.readShort();
        MsgTypeEnum msgType = MsgTypeEnum.getTypeById((int) msgTypeCode);
        if (Objects.isNull(msgType)){
            log.error("[decoder] not support msg type, msgTypeCode:{}",msgTypeCode);
            return;
        }
        short serializerType = in.readShort();
        Serializer serializer = Type2SSerializer.getSerializerByType(serializerType);
        if (Objects.isNull(serializer)){
            log.error("[decoder] not support serializer, type:{}",serializerType);
            return;
        }

        int msgLength = in.readInt();
        byte[] msg = new byte[msgLength];
        in.readBytes(msg);

        Object obj = serializer.deserialize(msg,msgTypeCode);
        out.add(obj);
    }
}
