package com.Backend.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class InsertCategoryRequest {

    @Getter
    @Setter
    private String categoryName;

    public boolean isValid(){
        return !categoryName.isEmpty();
    }

}
