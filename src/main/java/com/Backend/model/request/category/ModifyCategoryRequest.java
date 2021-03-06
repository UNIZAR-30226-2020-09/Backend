package com.Backend.model.request.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class ModifyCategoryRequest {

    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String categoryName;

    public boolean isValid(){
        return id != null && id >= 0 && categoryName != null && !categoryName.isEmpty();
    }

}
