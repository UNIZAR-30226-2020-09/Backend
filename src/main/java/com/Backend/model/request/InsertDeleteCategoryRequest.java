package com.Backend.model.request;

import com.Backend.model.Category;
import com.Backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class InsertDeleteCategoryRequest {

    @Getter
    @Setter
    private String name;

    public boolean isValid(){
        return name!=null;
    }

}
