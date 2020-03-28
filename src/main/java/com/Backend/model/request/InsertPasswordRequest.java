package com.Backend.model.request;

import com.Backend.model.Password;
import lombok.Getter;
import lombok.Setter;

public class InsertPasswordRequest {

    @Getter @Setter
    private String passwordName;
    @Getter @Setter
    private String password;
    @Getter @Setter
    private String optionalText;
    @Getter @Setter
    private String userName;
    @Getter @Setter
    Integer expirationTime;

    public boolean isValid(){
        return password != null && passwordName != null && expirationTime != null;
    }

    public Password getAsPassword(){
        Password pwd = new Password(password, passwordName, expirationTime);
        pwd.setOptionalText(optionalText);
        pwd.setUserName(userName);
        return pwd;
    }
}
