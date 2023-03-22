package com.example.gupshup.Models;

public class UserModel {
    String picture, email , password, username, userId, lastMsg;

    public UserModel(String picture, String email, String password, String username, String userId, String lastMsg) {
        this.picture = picture;
        this.email = email;
        this.password = password;
        this.username = username;
        this.userId = userId;
        this.lastMsg = lastMsg;
    }
    public  UserModel(){

    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }
    // Sign Up Constructor;
    public UserModel(String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
    }

}
