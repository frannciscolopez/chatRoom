package com.test.chat.model;

import io.netty.channel.group.ChannelGroup;

import java.util.HashMap;
import java.util.Map;

public class ChatRoom {
    public static Map<Room, ChannelGroup> map = new HashMap<>();
}
