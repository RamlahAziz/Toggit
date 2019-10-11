package com.strigiformes.teletalk;

import java.text.SimpleDateFormat;
import java.util.Date;


//cutomized message class might not use this
public class Message {

    private String idSender;
    private String idReceiver;
    private String textMessage;
    private long timestamp;
    private String threadId;
    private String time;
    private String token_receiver;
    private String sender_name;

    public Message() {
    }

    public Message(String idSender, String idReceiver, String textMessage, String time) {
        this.idSender = idSender;
        this.idReceiver = idReceiver;
        this.textMessage = textMessage;

        this.time=new SimpleDateFormat("hh:mm KK").format(new Date(timestamp));
    }

    public String getToken_receiver() {
        return token_receiver;
    }

    public void setToken_receiver(String token_receiver) {
        this.token_receiver = token_receiver;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(String idReceiver) {
        this.idReceiver = idReceiver;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    @Override
    public String toString() {
        return "Message{" +
                "idSender='" + idSender + '\'' +
                ", idReceiver='" + idReceiver + '\'' +
                ", textMessage='" + textMessage + '\'' +
                ", timestamp=" + timestamp +
                ", threadId='" + threadId + '\'' +
                ", time='" + time + '\'' +
                ", token_receiver='" + token_receiver + '\'' +
                ", sender_name='" + sender_name + '\'' +
                '}';
    }
}
