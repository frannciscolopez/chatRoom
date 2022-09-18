package com.test.chat.model;

import java.util.PriorityQueue;

public class Room {
    private String id;
    private String roomName;
    private PriorityQueue<String> messageHistory = new PriorityQueue<String>();
    private String numMessages;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public PriorityQueue<String> getMessageHistory() {
        return messageHistory;
    }

    public void setMessageHistory(PriorityQueue<String> messageHistory) {
        this.messageHistory = messageHistory;
    }

    public String getNumMessages() {
        return numMessages;
    }

    public void setNumMessages(String numMessages) {
        this.numMessages = numMessages;
    }

}
