package com.lot.chatandtranslate.Utils;

public class Chat {
    String userID, sms, status;

    public Chat() {

    }

    public Chat(String userID, String sms, String status) {
        this.userID = userID;
        this.sms = sms;
        this.status = status;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
