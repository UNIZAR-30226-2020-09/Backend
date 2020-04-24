package com.Backend.model.request.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class InsertCategoryRequest {

    @Getter
    @Setter
    private String categoryName;

    public boolean isValid(){
        return categoryName!= null && !categoryName.isEmpty();
    }

}
