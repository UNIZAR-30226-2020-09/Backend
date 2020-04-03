package com.Backend.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class InsertCategoryRequest {

    @Getter
    @Setter
    private String categoryName;

    public boolean isValid(){
        return categoryName!= null && !categoryName.isEmpty();
    }

}
