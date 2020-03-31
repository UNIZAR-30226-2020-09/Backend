package com.Backend.model.response;

import com.Backend.model.OwnsPassword;
import com.Backend.repository.IOwnsPassRepo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

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
    int rol;
    @Getter @Setter
    String optionalText;
    @Getter @Setter
    String userName;

    @Override
    public String toString() {
        return "PasswordResponse{" +
                "repoOwnsPass=" + repoOwnsPass +
                ", passId=" + passId +
                ", passwordName='" + passwordName + '\'' +
                ", catId=" + catId +
                ", categoryName='" + categoryName + '\'' +
                ", rol=" + rol +
                ", optionalText='" + optionalText + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }

    public PasswordResponse(OwnsPassword ops){

        this.passId = ops.getPassword().getId();
        this.passwordName = ops.getPassword().getPasswordName();
        this.catId = (ops.getPassword().getCategory()).getId();
        this.categoryName = (ops.getPassword().getCategory()).getCategoryName();
        this.rol = ops.getRol();
        this.optionalText = (ops.getPassword().getOptionalText());
        this.userName = (ops.getPassword().getUserName());
    }

}
