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
    //@Getter @Setter
    //Date expirationDate;

    public boolean isValid(){
        return password != null && passwordName != null;
    }

    public Password getAsPassword(){
        Password pwd = new Password(password, passwordName);
        pwd.setOptionalText(optionalText);
        pwd.setUserName(userName);
        return pwd;
    }
}
