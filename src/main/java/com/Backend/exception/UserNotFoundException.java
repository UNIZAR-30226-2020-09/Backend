package com.Backend.exception;

public class UserNotFoundException extends Exception {
    /* Excepción de usuario no encontrado */

    public UserNotFoundException(Long id){

        super("Usuario con id: " + id.toString() + " no ha sido encontrado");
    }
}
