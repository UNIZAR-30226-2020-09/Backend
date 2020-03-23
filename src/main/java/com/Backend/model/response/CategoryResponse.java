package com.Backend.model.response;

import com.Backend.model.Category;
import com.Backend.model.Password;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;

public class CategoryResponse implements Serializable {

    @Getter @Setter
    Long catId;
    @Getter @Setter
    String categoryName;
    @Getter @Setter
    String userMail;
    @Getter @Setter
    Long userId;
    @Getter @Setter
    Boolean hasPasswords;

    @Override
    public String toString() {
        return "CategoryResponse{" +
                "catId=" + catId +
                ", categoryName='" + categoryName + '\'' +
                ", userMail='" + userMail + '\'' +
                ", userId=" + userId +
                ", hasPasswords=" + hasPasswords +
                '}';
    }

    public CategoryResponse(Category cat){

        this.catId = cat.getId();
        this.userId = cat.getUsuario().getId();
        this.userMail = cat.getUsuario().getMail();
        this.categoryName = cat.getCategoryName();
        this.hasPasswords = (cat.getPasswordSet() != null
                && !cat.getPasswordSet().isEmpty());

    }

}
