package com.Backend.exception;

public class CategoryNotFoundException extends Exception {
    /* Excepción de categoría no encontrada */

    public CategoryNotFoundException(Long id){

        super("Usuario con id: " + id.toString() + " no ha sido encontrado");
    }
}
