package com.lot.chatandtranslate.Utils;

public class Posts {
    private String date,postDesc, postImageUri, userProfileImageUri, username;

    public Posts() {

    }

    public Posts(String date, String postDesc, String postImageUri, String userProfileImageUri, String username) {
        this.date = date;
        this.postDesc = postDesc;
        this.postImageUri = postImageUri;
        this.userProfileImageUri = userProfileImageUri;
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostDesc() {
        return postDesc;
    }

    public void setPostDesc(String postDesc) {
        this.postDesc = postDesc;
    }

    public String getPostImageUri() {
        return postImageUri;
    }

    public void setPostImageUrl(String postImageUri) {
        this.postImageUri = postImageUri;
    }

    public String getUserProfileImageUri() {
        return userProfileImageUri;
    }

    public void setUserProfileImageUrl(String userProfileImageUri) {
        this.userProfileImageUri = userProfileImageUri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
