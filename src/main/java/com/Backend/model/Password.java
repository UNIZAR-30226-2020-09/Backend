package com.Backend.model;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
public class Password {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Getter
    @Setter
    private String passwordName;

    protected Password() {}

    public Password(String categoryName) {
    }

    @Override
    public String toString() {
        return "User{" +
                '}';
    }

    public void displayPasswordInfo(){
        System.out.println("Password name is: " + "");
    }


}