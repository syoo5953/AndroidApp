package com.lot.chatandtranslate.Utils;

public class Friends {
    private String job, profileImageUri, username;

    public Friends() {

    }

    public Friends(String job, String profileImageUri, String username) {
        this.job = job;
        this.profileImageUri = profileImageUri;
        this.username = username;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
