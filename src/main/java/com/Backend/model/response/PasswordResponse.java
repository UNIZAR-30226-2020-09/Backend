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

    @Override
    public String toString() {
        return "CategoryResponse{" +
                "passId=" + passId +
                ", passwordName='" + passwordName + '\'' +
                ". catId=" + catId +
                ", categoryName='" + categoryName + '\'' +
                ", rol='" + rol +
                '}';
    }

    public PasswordResponse(OwnsPassword ops){

        this.passId = ops.getPassword().getId();
        this.passwordName = ops.getPassword().getPasswordName();
        this.catId = (ops.getPassword().getCategory()).getId();
        this.categoryName = (ops.getPassword().getCategory()).getCategoryName();
        this.rol = ops.getRol();
    }

}
