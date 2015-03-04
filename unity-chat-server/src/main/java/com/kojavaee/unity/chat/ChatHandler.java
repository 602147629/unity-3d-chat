package com.kojavaee.unity.chat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import javax.rmi.CORBA.Util;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class ChatHandler extends ChannelInboundHandlerAdapter {
    // 解码器的类样式
    private Schema<ChatMsg> schema = RuntimeSchema.getSchema(ChatMsg.class);
    // 保存所有连接的channel, 用于消息广播
    private static ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        allChannels.add(ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ByteBuf)) {
            return;
        }
        ByteBuf data = (ByteBuf) msg;
        // 前面4个字节存储长度
        if (data.readableBytes() < 4) {
            return;
        }
        data.markReaderIndex();
        // 解析出消息体的长度
        byte[] lenBytes = new byte[4];
        data.readBytes(lenBytes);
        int length = bytesToInt(lenBytes, 0);
        // 消息体长度不够，继续等待
        if (data.readableBytes() < length) {
            data.resetReaderIndex();
            return;
        }
        // 解析出消息体
        byte[] dataBytes = new byte[length];
        data.readBytes(dataBytes);

        // Protobuf解码
        ChatMsg chatMsg = new ChatMsg();
        ProtobufIOUtil.mergeFrom(dataBytes, chatMsg, schema);

        //为了测试Protobuf编码，将解码出来二进制消息再编码成Protobuf对象
        LinkedBuffer buffer = LinkedBuffer.allocate(1024);
        //消息体的格式：消息体长度+消息体
        byte[] msgData = ProtobufIOUtil.toByteArray(chatMsg, schema, buffer);
        ByteBuf sendData = Unpooled.copiedBuffer(intToBytes(msgData.length), msgData);
        //广播
        allChannels.write(sendData);
        allChannels.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // TODO Auto-generated method stub
        super.exceptionCaught(ctx, cause);
    }
    
    /**
     * bytes转换成int
     * @param data
     * @param offset
     * @return
     */
    public static int bytesToInt(byte[] data, int offset) {
           int num = 0;
           for (int i = offset; i < offset + 4; i++) {
            num <<= 8;
            num |= (data[i] & 0xff);
           }
           return num;
        }
    
    /**
     * int转换成byte数组
     * @param num
     * @return
     */
    public static byte[] intToBytes(int num) {   
        byte[] b = new byte[4];
           for (int i = 0; i < 4; i++) {
            b[i] = (byte) (num >>> (24 - i * 8));
           }
           return b;
    }
}
