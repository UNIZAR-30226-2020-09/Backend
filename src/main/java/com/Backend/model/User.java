package com.Backend.model;

public class User {

    private String userName;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public User(String userName) {
        this.userName = userName;
    }

    public void displayUserInfo(){
        System.out.println("User name is: " + userName);
    }

}

