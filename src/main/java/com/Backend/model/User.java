package com.Backend.model;
import javax.persistence.*;

@Entity
@Table(name = "Pandora_user")
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String mail;
    private String userName;

    protected User() {}

    public User(String userName, String mail) {
        this.userName = userName;
        this.mail = mail;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUserName() {
        return userName;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", mail='" + mail + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void displayUserInfo(){
        System.out.println("User name is: " + userName);
    }


}

