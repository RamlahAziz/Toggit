package com.strigiformes.teletalk.CustomObjects;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

    private String name;
    private String profilePicture;
    private String uid;
    private String deviceToken;
    private String phoneNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", uid='" + uid + '\'' +
                ", deviceToken='" + deviceToken + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(getName(), user.getName()) &&
                Objects.equals(getProfilePicture(), user.getProfilePicture()) &&
                Objects.equals(getUid(), user.getUid()) &&
                Objects.equals(getDeviceToken(), user.getDeviceToken()) &&
                Objects.equals(getPhoneNumber(), user.getPhoneNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getProfilePicture(), getUid(), getDeviceToken(), getPhoneNumber());
    }


}
