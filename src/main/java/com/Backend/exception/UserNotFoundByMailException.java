package com.Backend.exception;

public class UserNotFoundByMailException extends Exception {
    /* Excepción de usuario no encontrado */

    public UserNotFoundByMailException(String mail){

        super("Usuario con mail: " + mail + " no ha sido encontrado");
    }
}
