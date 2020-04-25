package com.Backend.model.request.groupPassword;

import com.Backend.model.Password;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;

@AllArgsConstructor
public class InsertGroupPasswordRequest {

    @Getter
    @Setter
    private String passwordName;

    @Getter
    @Setter
    private Long passwordCategoryId;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String optionalText;

    @Getter
    @Setter
    private String userName;

    @Getter
    @Setter
    Integer expirationTime;

    @Getter
    @Setter
    LinkedList<String> usuarios;

    @Getter
    @Setter
    String masterPassword;

    public boolean isValid(){
        return  password!=null && !password.isEmpty() &&
                passwordName!=null && !passwordName.isEmpty() &&
                expirationTime != null && passwordCategoryId != null;
    }

    public Password getAsPassword() {
        Password pwd = new Password(password, passwordName, expirationTime);
        pwd.setOptionalText(optionalText);
        pwd.setUserName(userName);
        return pwd;
    }
}