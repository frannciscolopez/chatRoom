package com.test.chat;

import java.net.SocketAddress;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import com.test.chat.service.UserService;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.AttributeKey;

public class Utils {

    public static void log(String message) {
        System.out.println(message);
    }

    public static void log(SocketAddress socketAddress, String message) {
        System.out.println("< " + socketAddress + " > : " + message);
    }

    public static Object getName(SocketAddress socketAddress) {

        String userName = "user"+socketAddress;
        return userName;
    }

    public static int getUsers(ChannelGroup channelGroup) {
        if (channelGroup == null) {
            return 0;
        }
        return channelGroup.size();
    }

    public static boolean notAchievedMaximumMessages(ChannelHandlerContext ctx) {
        AttributeKey<Object> attributeKey = AttributeKey.valueOf("NUM_MSG");
        if (getNumMsgs(ctx, attributeKey) < 30 && notReachedTimeLimit(ctx)) {
            return true;
        } else if (!notReachedTimeLimit(ctx)) {
            ctx.channel().attr(AttributeKey.valueOf("NUM_MSG")).set("0");
            return true;
        }
        return false;
    }

    private static boolean notReachedTimeLimit(ChannelHandlerContext ctx) {
        String limitHour = ctx.channel().attr(AttributeKey.valueOf("LIMIT_HOUR")).get().toString();
        Long minutes = ChronoUnit.MINUTES.between(LocalTime.parse(limitHour), LocalTime.now());
        return minutes < 1;
    }

    public static int getNumMsgs(ChannelHandlerContext ctx, AttributeKey<Object> attributeKey) {
        String numMsgs = (String) ctx.channel().attr(attributeKey).get();
        int numMsgsAsInt = Integer.parseInt(numMsgs);
        return numMsgsAsInt;
    }

    public static void addChannelToGroup(ChannelHandlerContext ctx, ChannelGroup channelGroup, String roomToJoin) {
        channelGroup.add(ctx.channel());
        ctx.channel().attr(AttributeKey.valueOf("CHATROOM")).set(roomToJoin);
    }

    public static boolean isFirstMessage(ChannelHandlerContext ctx) {
        return getNumMsgs(ctx, AttributeKey.valueOf("NUM_MSG")) == 0;
    }

    public static void setTimeOfFirstMessage(ChannelHandlerContext ctx) {
        LocalTime timeOfFirstMessage = LocalTime.now();
        UserService.setKeyValue(ctx, timeOfFirstMessage, "LIMIT_HOUR");
    }
    
    public static void showMessage (ChannelHandlerContext ctx, String msg) {
        ctx.writeAndFlush(" > " + UserService.getKeyValue(ctx, "USERNAME") + ": " + msg + "\n");
    }

    public static void addNumMsgs(ChannelHandlerContext ctx) {
        int totalNumber = getNumMsgs(ctx, AttributeKey.valueOf("NUM_MSG")) + 1;
        UserService.setKeyValue(ctx, String.valueOf(totalNumber), "NUM_MSG");
    }

}


