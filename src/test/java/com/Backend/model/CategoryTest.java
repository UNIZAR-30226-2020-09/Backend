package com.Backend.model;

import org.junit.jupiter.api.Test;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CategoryTest {

    @Test
    public void testBasicSettersAndGettersCategory(){

        User usuario = new User("mail1","mp1");
        User usuario2 = new User("mail2","mp2");
        Category cat = new Category("cat1",usuario);
        Category cat2 = new Category("cat2",usuario2);

        assertNotEquals(cat.getUsuario(),cat2.getUsuario());
        assertNotNull(cat.getUsuario());
        assertNotNull(cat2.getUsuario());
        assertNotNull(cat.getCategoryName());
        assertNotNull(cat2.getCategoryName());
        assertNotEquals(cat.getCategoryName(),cat2.getCategoryName());

        cat.setCategoryName("cat2");
        assertNotNull(cat.getCategoryName());
        assertNotNull(cat2.getCategoryName());
        assertEquals(cat.getCategoryName(),cat2.getCategoryName());
    }
}
