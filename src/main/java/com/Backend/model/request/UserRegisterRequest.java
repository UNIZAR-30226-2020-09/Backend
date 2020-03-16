package com.Backend.model.request;

import com.Backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {

    @Getter
    private String username;
    @Getter
    @Setter
    private String password;

    public boolean isValid(){
        return username!=null && password!=null;
    }

    public User getAsUser(){
        return new User(username,password);
    }
}
