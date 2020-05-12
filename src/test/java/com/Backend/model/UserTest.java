package com.Backend.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {

    @Test
    public void testBasicSettersAndGettersUser(){

        User usuario = new User();
        usuario.setId("1L");
        usuario.setMail("email");
        usuario.setMasterPassword("masterPass");
        assertEquals("1L",usuario.getId());
        assertEquals("email",usuario.getMail());
        assertEquals("masterPass",usuario.getMasterPassword());

        User usuario2 = new User("correo","pass");
        usuario2.setId("2L");
        usuario2.setMail("email2");
        usuario2.setMasterPassword("masterPass2");
        assertEquals("2L",usuario2.getId());
        assertEquals("email2",usuario2.getMail());
        assertEquals("masterPass2",usuario2.getMasterPassword());
    }
}
