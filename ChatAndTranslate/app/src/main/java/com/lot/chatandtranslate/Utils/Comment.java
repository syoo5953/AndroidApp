package com.lot.chatandtranslate.Utils;

public class Comment {
    private String username, profileImageUri, comment;

    public Comment() {

    }

    public Comment(String username, String profileImageUri, String comment) {
        this.username = username;
        this.profileImageUri = profileImageUri;
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
