package com.test.chat.service;

import java.util.Iterator;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Map.Entry;

import com.test.chat.Utils;
import com.test.chat.model.ChatRoom;
import com.test.chat.model.Room;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.ImmediateEventExecutor;

public class RoomService {

    public static void getCurrentRooms(ChannelHandlerContext ctx) {
        ChatRoom.map.entrySet()
                .stream()
                .forEach(room -> ctx.writeAndFlush(">" + room.getKey().getRoomName() + "\n"));
    }

    public static void joinRoom(String roomToJoin, ChannelHandlerContext ctx) {
        Room room = getRoomFromRoomName(ctx, roomToJoin);
        ChannelGroup channelGroup = ChatRoom.map.get(room);
        ctx.writeAndFlush(">Welcome to  " + roomToJoin + " Room, there are " + Utils.getUsers(channelGroup)
                + " users connected. \n");
        getAndSendHistoricalMessages(room, ctx);

        if (channelGroup == null) {

            Room newRoom = buildRoom(roomToJoin);
            channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
            Utils.addChannelToGroup(ctx, channelGroup, roomToJoin);
            ChatRoom.map.put(newRoom, channelGroup);

        } else {

            Utils.addChannelToGroup(ctx, channelGroup, roomToJoin);
        }
    }

    public static void getAndSendHistoricalMessages(Room room, ChannelHandlerContext ctx) {
        if (room != null) {
            PriorityQueue<String> messageHistory = room.getMessageHistory();
            Iterator<String> iterator = messageHistory.iterator();
            for (int i = 0; i < 5; i++) {
                if (iterator.hasNext()) {
                    ctx.writeAndFlush(">" + iterator.next() + "\n");
                }

            }

        }
    }

    public static void leaveRoom(ChannelHandlerContext ctx) {
        Object nameRoom = UserService.getKeyValue(ctx, "CHATROOM");
        Room room = getRoomFromRoomName(ctx, nameRoom.toString());
        ChannelGroup channelGroup = ChatRoom.map.get(room);
        channelGroup.remove(ctx.channel());
        System.out.println(channelGroup.size());
        if (Utils.getUsers(channelGroup) == 0) {
            ChatRoom.map.remove(room);
        }
    }

    public static void putMessageInChat(ChannelHandlerContext ctx, String msg) {
        Object nameRoom = UserService.getKeyValue(ctx, "CHATROOM");
        Room room = getRoomFromRoomName(ctx, nameRoom.toString());
        ChannelGroup channelGroup = ChatRoom.map.get(room);
        if (Utils.isFirstMessage(ctx)) {
            Utils.setTimeOfFirstMessage(ctx);
        }
        if (!channelGroup.isEmpty() && Utils.notAchievedMaximumMessages(ctx)) {
            addMessageToHistoricalMessages(room, ctx, msg);
            Utils.showMessage(ctx, msg);
            Utils.addNumMsgs(ctx);
            sendMessageToOtherUsers(channelGroup, ctx, msg);
        }
    }

    private static void sendMessageToOtherUsers(ChannelGroup channelGroup, ChannelHandlerContext ctx,
            String msg) {
        String id = ctx.channel().id().asLongText();
        for (Channel channel : channelGroup) {
            String channelId = channel.id().asLongText();
            if (!id.equals(channelId)) {
                channel.writeAndFlush(UserService.getKeyValue(ctx, "USERNAME") + ": " + msg + "\n");
            }

        }
    }


    private static void addMessageToHistoricalMessages(Room room, ChannelHandlerContext ctx, String msg) {
        Object userName = UserService.getKeyValue(ctx, "USERNAME");
        room.getMessageHistory().add("" + userName + ": " + msg);
    }

    public static Room getRoomFromRoomName(ChannelHandlerContext ctx, String roomToJoin) {
        Optional<Entry<Room, ChannelGroup>> room = ChatRoom.map.entrySet()
                .stream()
                .filter(e -> e.getKey().getRoomName().equals(roomToJoin))
                .findFirst();

        if (room.isPresent()) {
            return room
                    .get()
                    .getKey();
        }
        return null;
    }

    public static Room buildRoom(String roomToJoin) {
        Room newRoom = new Room();
        newRoom.setRoomName(roomToJoin);
        return newRoom;
    }

}
