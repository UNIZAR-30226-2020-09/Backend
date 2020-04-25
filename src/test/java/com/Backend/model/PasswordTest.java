package com.Backend.model;

import org.junit.jupiter.api.Test;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PasswordTest {

    @Test
    public void testBasicSettersAndGettersPassword() {

        Password pass = new Password("password", "passwordName", 0);
        Password passwordTwo = new Password("password2", "passwordName2", 90);
        pass.setOptionalText("Contraseña número 1.");

        User usuario2 = new User("mail2","mp2");
        Category cat = new Category("cat1", usuario2);
        Category cat2 = new Category("cat2",usuario2);

        pass.setCategory(cat2);
        passwordTwo.setCategory(cat);

        assertEquals(pass.getOptionalText(), "Contraseña número 1.");
        assertEquals(pass.getPasswordName(), "passwordName");
        assertEquals(pass.getPassword(), "password");
        assertNotEquals(passwordTwo.getCategory(), pass.getCategory());
    }
}
