package com.Backend.model.request;

import com.Backend.model.Password;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class ListPasswordRequest {

    @Getter
    @Setter
    private String masterPassword;

    public boolean isValid(){
        return masterPassword!=null && !masterPassword.isEmpty();
    }
}

