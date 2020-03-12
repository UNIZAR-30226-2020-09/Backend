package com.Backend.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordTest {

    @Test
    public void testBasicSettersAndGettersPassword() {

        User usuario = new User("email", "masterPass");
        Category cat = new Category("cat1", usuario);
        Password passwd = new Password("pass1", "namePass1");

        assertNull(passwd.getCategory());
        assertEquals(passwd.getPassword(), "pass1");
        assertEquals(passwd.getPasswordName(), "namePass1");
        assertNull(passwd.getUserName());
        assertNull(passwd.getCategory());

        passwd.setCategory(cat);
        passwd.setOptionalText("optional");
        passwd.setUserName("usuarioInexistente");

        assertEquals(passwd.getCategory(),cat);
        assertEquals(passwd.getOptionalText(),"optional");
        assertEquals(passwd.getUserName(),"usuarioInexistente");
    }
}
