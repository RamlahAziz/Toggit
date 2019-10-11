package com.strigiformes.teletalk;

import java.io.Serializable;

public class ChatListItem implements Serializable {

    private String name;
    private String from;
    private String to;
    private String timeStamp;
    private String msgPreview;
    private String pictureUri;
    private String chatId;
    private String driverPhone;
    private String customerPhone;

    @Override
    public String toString() {
        return "ChatListItem{" +
                "name='" + name + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", msgPreview='" + msgPreview + '\'' +
                ", pictureUri='" + pictureUri + '\'' +
                ", chatId='" + chatId + '\'' +
                ", driverPhone='" + driverPhone + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                '}';
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getPictureUri() {
        return pictureUri;
    }

    public void setPictureUri(String pictureUri) {
        this.pictureUri = pictureUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMsgPreview() {
        return msgPreview;
    }

    public void setMsgPreview(String msgPreview) {
        this.msgPreview = msgPreview;
    }
}