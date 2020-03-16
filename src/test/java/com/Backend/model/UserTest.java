package com.Backend.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {

    @Test
    public void testBasicSettersAndGettersUser(){

        User usuario = new User();
        usuario.setId(1L);
        usuario.setUsername("email");
        usuario.setPassword("masterPass");
        assertEquals(1L,usuario.getId());
        assertEquals("email",usuario.getUsername());
        assertEquals("masterPass",usuario.getPassword());

        User usuario2 = new User("correo","pass");
        usuario2.setId(2L);
        usuario2.setUsername("email2");
        usuario2.setPassword("masterPass2");
        assertEquals(2L,usuario2.getId());
        assertEquals("email2",usuario2.getUsername());
        assertEquals("masterPass2",usuario2.getPassword());
    }
}
