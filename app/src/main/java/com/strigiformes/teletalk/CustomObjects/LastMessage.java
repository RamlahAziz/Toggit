package com.strigiformes.teletalk.CustomObjects;

public class LastMessage {

    private Message lastMsg;
    private String number;

    public LastMessage(Message lastMsg, String number) {
        this.lastMsg = lastMsg;
        this.number = number;
    }

    public Message getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(Message lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "LastMessage{" +
                "lastMsg=" + lastMsg +
                ", number='" + number + '\'' +
                '}';
    }
}
