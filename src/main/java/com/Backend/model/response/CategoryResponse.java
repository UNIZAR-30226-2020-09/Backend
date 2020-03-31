package com.Backend.model.response;

import com.Backend.model.Category;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class CategoryResponse implements Serializable {

    @Getter @Setter
    Long catId;
    @Getter @Setter
    String categoryName;

    @Override
    public String toString() {
        return "CategoryResponse{" +
                "catId=" + catId +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }

    public CategoryResponse(Category cat){
        this.catId = cat.getId();
        this.categoryName = cat.getCategoryName();
    }

}
