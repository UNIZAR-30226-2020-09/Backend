package com.Backend.model.request;

import com.Backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest {

    @Getter
    @Setter
    private String mail;
    @Getter
    @Setter
    private String masterPassword;

    @Getter
    @Setter
    private String verificationCode;

    public boolean isValid(){
        return mail != null && !mail.isEmpty() && masterPassword!=null && !masterPassword.isEmpty()
                && verificationCode != null && !verificationCode.isEmpty();
    }

    public User getAsUser(){
        return new User(mail,masterPassword);
    }
}
