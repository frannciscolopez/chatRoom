package com.test.chat.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class UserService {
    public static void setKeyValue(ChannelHandlerContext ctx, Object value, String key) {
        ctx.channel().attr(AttributeKey.valueOf(key)).set(value);
    }

    public static Object getKeyValue(ChannelHandlerContext ctx, String key) {
        return ctx.channel().attr(AttributeKey.valueOf(key)).get();
    }

    public static void changeNickName(ChannelHandlerContext ctx, String newName) {
        setKeyValue(ctx, newName, "USERNAME");
        ctx.writeAndFlush("Hello " + newName + "\n");
    }
}
