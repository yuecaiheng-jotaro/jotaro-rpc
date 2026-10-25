package com.jotaro.serializer;

import com.jotaro.common.MsgTypeEnum;
import com.jotaro.pojo.req.RpcRequest;
import com.jotaro.pojo.resp.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: 自定义编码器
 * @author: yuecaiheng.jotaro
 * @date: 2026/10/25 16:33
 */
@Slf4j
@AllArgsConstructor
public class JotaroEncoder extends MessageToByteEncoder {

    private Serializer serializer;


    @Override
    protected void encode(final ChannelHandlerContext ctx, final Object msg, final ByteBuf out) throws Exception {
        if (msg instanceof RpcRequest){
            out.writeShort(MsgTypeEnum.REQ.getCode());
        }else if (msg instanceof RpcResponse){
            out.writeShort(MsgTypeEnum.RESP.getCode());
        }
        out.writeShort(serializer.getSerializeType());
        byte[] bytes = serializer.serialize(msg);
        out.writeInt(Integer.valueOf(bytes.length));
        out.writeBytes(bytes);
    }
}
