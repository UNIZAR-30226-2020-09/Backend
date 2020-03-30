package com.Backend.model.request;

import com.Backend.exception.CategoryNotFoundException;
import com.Backend.model.Password;
import com.Backend.repository.ICatRepo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

public class InsertPasswordRequest {

    @Getter @Setter
    private String passwordName;
    @Getter @Setter
    private Long passwordCategoryId;
    @Getter @Setter
    private String password;
    @Getter @Setter
    private String optionalText;
    @Getter @Setter
    private String userName;
    @Getter @Setter
    Integer expirationTime;

    public boolean isValid(){
        return password != null && passwordName != null && expirationTime != null
                && passwordCategoryId != null;
    }

    public Password getAsPassword() {
        Password pwd = new Password(password, passwordName, expirationTime);
        pwd.setOptionalText(optionalText);
        pwd.setUserName(userName);
        return pwd;
    }
}
