package com.Backend.model.response;

import com.Backend.model.OwnsPassword;
import com.Backend.model.Password;
import com.Backend.repository.IOwnsPassRepo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

public class PasswordResponse implements Serializable {

    @Autowired
    IOwnsPassRepo repoOwnsPass;

    @Getter @Setter
    Long passId;
    @Getter @Setter
    String passwordName;
    @Getter @Setter
    Long catId;
    @Getter @Setter
    String categoryName;
    @Getter @Setter
    String password;
    @Getter @Setter
    int rol;
    @Getter @Setter
    String optionalText;
    @Getter @Setter
    String userName;
    @Getter @Setter
    long expirationDate;

    public PasswordResponse(OwnsPassword ops){

        this.passId = ops.getPassword().getId();
        this.passwordName = ops.getPassword().getPasswordName();
        this.catId = (ops.getPassword().getCategory()).getId();
        this.categoryName = (ops.getPassword().getCategory()).getCategoryName();
        this.rol = ops.getRol();
        this.optionalText = (ops.getPassword().getOptionalText());
        this.userName = (ops.getPassword().getUserName());
        this.password = ops.getPassword().getPassword();

        LocalDate actual = LocalDate.now();
        LocalDate fin = ops.getPassword().getExpirationTime();
        expirationDate = ChronoUnit.DAYS.between(actual, fin);
    }

    public PasswordResponse(Password p){

        this.passId = p.getId();
        this.passwordName = p.getPasswordName();
        this.catId = p.getCategory().getId();
        this.categoryName = (p.getCategory()).getCategoryName();
        this.rol = 1;
        this.optionalText = (p.getOptionalText());
        this.userName = (p.getUserName());
        this.password = p.getPassword();

        LocalDate actual = LocalDate.now();
        LocalDate fin = p.getExpirationTime();
        expirationDate = ChronoUnit.DAYS.between(actual, fin);
    }

}
