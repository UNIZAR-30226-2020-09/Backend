package com.Backend.model.request.user;

import com.Backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {

    @Getter
    @Setter
    private String mail;
    @Getter
    @Setter
    private String masterPassword;

    public boolean isValid(){
        return mail != null && !mail.isEmpty() && masterPassword!=null && !masterPassword.isEmpty();
    }

    public User getAsUser(){
        return new User(mail,masterPassword);
    }
}
