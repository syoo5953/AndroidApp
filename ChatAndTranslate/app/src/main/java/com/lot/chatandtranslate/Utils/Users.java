package com.lot.chatandtranslate.Utils;

public class Users {
    private String username, age, city, job, profileImage, status;

    public Users() {

    }

    public Users(String username, String age, String city, String job, String profileImage, String status) {
        this.username = username;
        this.age = age;
        this.city = city;
        this.job = job;
        this.profileImage = profileImage;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
