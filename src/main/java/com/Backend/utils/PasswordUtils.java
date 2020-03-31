package com.Backend.utils;

import com.Backend.model.Category;
import com.Backend.model.Password;
import com.Backend.model.User;
import com.Backend.repository.ICatRepo;
import com.Backend.repository.IPassRepo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PasswordUtils {

    public static void modifyPasswordsAtCategoryDelete(Category cat, IPassRepo repoPass,
                                                       ICatRepo repoCat, User usuario){
        List<Password> passwords = repoPass.findByCategory(cat);
        Category sinCat = CategoryUtils.getSinCategoria(repoCat,usuario);

        for(Password pass : passwords){
            pass.setCategory(sinCat);
            repoPass.save(pass);
        }
    }
}
