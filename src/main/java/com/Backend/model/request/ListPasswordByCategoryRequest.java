package com.Backend.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class ListPasswordByCategoryRequest {

    @Getter
    @Setter
    private String masterPassword;

    @Getter
    @Setter
    private Long idCat;

    public boolean isValid(){
        return masterPassword!=null && !masterPassword.isEmpty() && idCat!=null;
    }
}

