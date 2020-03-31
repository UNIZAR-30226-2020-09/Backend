package com.Backend.model.request;

import com.Backend.model.Category;
import com.Backend.model.Password;
import com.Backend.model.User;
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
        return categoryName != null;
    }

}
