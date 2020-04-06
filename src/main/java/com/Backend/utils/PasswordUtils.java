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
import java.util.Random;
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

    public static String generateStrongPassword(boolean min, boolean may, boolean numbers, boolean other, int longitud){

        Random r = new Random();
        String minus = "abcdefghijklmnopqrstuvwxyz";
        String mayus = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digit = "0123456789";
        String others = "@#%&?!+-_.;.<>*^";

        String alphabet = "";
        String password = "";

        if(min)
            alphabet += minus;
        if(may)
            alphabet += mayus;
        if(numbers)
            alphabet += digit;
        if(other)
            alphabet += others;

        for (int i = 0; i < longitud; i++) {
            password += alphabet.charAt(r.nextInt(alphabet.length()));
        }

        return password;
    }


}
