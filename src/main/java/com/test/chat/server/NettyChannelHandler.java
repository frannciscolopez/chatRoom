package com.test.chat.server;

import com.test.chat.Utils;
import com.test.chat.service.RoomService;
import com.test.chat.service.UserService;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

public class NettyChannelHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String userName = Utils.getName(ctx.channel().remoteAddress()).toString();
        ctx.channel().attr(AttributeKey.valueOf("IP")).set(ctx.channel().remoteAddress());
        ctx.channel().attr(AttributeKey.valueOf("USERNAME")).set(userName);
        ctx.channel().attr(AttributeKey.valueOf("NUM_MSG")).set("0");
        ctx.writeAndFlush("Hello " + userName + "\n");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        String[] command = msg.split("\\s+");

        switch (command[0]) {
            case "/nick":
                String newName = command[1];
                UserService.changeNickName(ctx, newName);
                break;

            case "/list":
                RoomService.getCurrentRooms(ctx);
                break;

            case "/join":
                String roomToJoin = command[1];
                RoomService.joinRoom(roomToJoin, ctx);
                break;

            case "/exit":
                RoomService.leaveRoom(ctx);
                this.channelInactive(ctx);
                break;

            default:
                RoomService.putMessageInChat(ctx, msg);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Utils.log(ctx.channel().remoteAddress(), "Channel Inactive");
    }
}
