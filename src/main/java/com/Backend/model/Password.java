package com.Backend.model;

public class Password {

    private String pass;

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void displayUserInfo() {
        System.out.println("User name is: " + pass);
    }

}
