package com.Backend.model.response;

import com.Backend.model.User;
import lombok.Getter;
import lombok.Setter;

public class UserResponse {

    @Getter @Setter
    Long id;
    @Getter @Setter
    String mail;
    @Getter @Setter
    String masterPassword;
    @Getter @Setter
    String token;
    @Getter @Setter
    Boolean hasPasswords;
    @Getter @Setter
    Boolean hasCategories;

    public UserResponse(User usuario){

        this.id = usuario.getId();
        this.mail = usuario.getMail();
        this.token = usuario.getToken();
        this.masterPassword = usuario.getMasterPassword();
        hasPasswords = (usuario.getPasswordSet() != null
                && !usuario.getPasswordSet().isEmpty());
        hasCategories = (usuario.getCategorySet() != null
                && !usuario.getCategorySet().isEmpty());

    }
}
