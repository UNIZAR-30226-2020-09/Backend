package com.Backend.exception;

import lombok.Getter;
import lombok.Setter;

public class UserNotFoundException extends Exception {
    /* Excepción de usuario no encontrado */

    @Getter
    @Setter
    public String mail = null;

    public UserNotFoundException(Long id){
        super("Usuario con id: " + id.toString() + " no ha sido encontrado.");
    }

    public UserNotFoundException(String mail){
        super("Usuario con mail: " + mail + " no ha sido encontrado.");
        this.mail = mail;
    }
}
