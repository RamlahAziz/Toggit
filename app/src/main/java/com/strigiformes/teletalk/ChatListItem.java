package com.strigiformes.teletalk;

import java.io.Serializable;

public class ChatListItem implements Serializable {

    private String name;
    private String timeStamp;
    private String msgPreview;
    private String pictureUri;
    private String chatId;
    private String fromPhone;
    private String toPhone;

    public ChatListItem() {
    }

    public ChatListItem(String name, String toPhone) {
        this.name = name;
        this.toPhone = toPhone;
    }

    @Override
    public String toString() {
        return "ChatListItem{" +
                "name='" + name + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", msgPreview='" + msgPreview + '\'' +
                ", pictureUri='" + pictureUri + '\'' +
                ", chatId='" + chatId + '\'' +
                ", fromPhone='" + fromPhone + '\'' +
                ", toPhone='" + toPhone + '\'' +
                '}';
    }

    public String getToPhone() {
        return toPhone;
    }

    public void setToPhone(String toPhone) {
        this.toPhone = toPhone;
    }

    public String getFromPhone() {
        return fromPhone;
    }

    public void setFromPhone(String fromPhone) {
        this.fromPhone = fromPhone;
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