package com.example.chatapp.RoomDataBase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;

@Entity(tableName = "Chats")
public class ChatData {

    @PrimaryKey(autoGenerate = true)
    public int id;

    private String sender;
    private String receiver; //個人聊天室的話可加 receiver
    private String message;
    private String senderName;
    private int seenPeople; //記錄看過的人
    private long msgTime;

    public ChatData(String sender, String receiver, String message, String senderName, int seenPeople, long msgTime) {
        this.sender = sender;
        this.receiver =receiver;
        this.message= message;
        this.senderName=senderName;
        this.seenPeople=seenPeople;
        this.msgTime=msgTime;
    }

    public ChatData(){

    }

    public String getSender() {
        return sender;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setSeenPeople(int seenPeople) {
        this.seenPeople = seenPeople;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }

    public int getId() {
        return id;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderName() {
        return senderName;
    }

    public int getSeenPeople() {
        return seenPeople;
    }

    public long getMsgTime() {
        return msgTime;
    }
}
