package com.Backend.exception;

public class PasswordNotFoundException extends Exception {
    /* Excepción de usuario no encontrado */

    public PasswordNotFoundException(Long id){

        super("Password con id: " + id.toString() + " no ha sido encontrado");
    }
}
