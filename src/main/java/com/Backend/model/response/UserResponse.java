package com.Backend.model.response;

import com.Backend.model.User;
import lombok.Getter;
import lombok.Setter;

public class UserResponse {

    @Getter @Setter
    String id;
    @Getter @Setter
    String mail;
    @Getter @Setter
    String masterPassword;
    @Getter @Setter
    Boolean mailVerified;
    @Getter @Setter
    Boolean hasPasswords;
    @Getter @Setter
    Boolean hasCategories;

    public UserResponse(User usuario){

        id = usuario.getId();
        mail = usuario.getMail();
        masterPassword = usuario.getMasterPassword();
        hasPasswords = (usuario.getPasswordSet() != null
                && !usuario.getPasswordSet().isEmpty());
        hasCategories = (usuario.getCategorySet() != null
                && !usuario.getCategorySet().isEmpty());

        mailVerified = usuario.getMailVerified();
    }
}
