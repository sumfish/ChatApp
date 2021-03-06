package com.example.chatapp.Model;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class Chat {


    private String receiver; //個人聊天室的話可加 receiver
    private String message;
    private String sender;
    private String senderName;
    private HashMap<String,Boolean> seenPeople; //記錄看過的人
    private long msgTime;

    public Chat(String sender, String receiver, String message, String senderName, HashMap<String,Boolean> seenPeople, long time) { //要跟firebase裡的setting一樣
        this.sender = sender;
        this.receiver =receiver;
        this.message= message;
        this.senderName=senderName;
        this.msgTime=time;
        this.seenPeople=seenPeople;

        //msgTime= new Date().getTime();
    }

    public Chat(){

    }

    public HashMap<String,Boolean> getSeenPeople() {
        return seenPeople;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getMessage() {
        return message;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }


    public long getMsgTime() {
        return msgTime;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setSeenPeople(HashMap<String,Boolean> seenPeople) {
        this.seenPeople = seenPeople;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }
}
