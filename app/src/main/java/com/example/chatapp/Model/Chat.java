package com.example.chatapp.Model;

import java.util.Date;

public class Chat {


    private String receiver; //個人聊天室的話可加 receiver
    private String messag;
    private String sender;
    private long msgTime;

    public Chat(String sender, String messag) { //要跟firebase裡的setting一樣
        this.sender = sender;
        this.receiver =receiver;
        this.messag= messag;

        msgTime= new Date().getTime();
    }

    public Chat(){

    }

    public String getMessage() {
        return messag;
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
        this.messag = message;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }


    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }
}
