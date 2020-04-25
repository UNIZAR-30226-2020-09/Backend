package com.Backend.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class DeleteByIdRequest {

    @Getter
    @Setter
    private Long id;

    public boolean isValid(){
        return id != null;
    }

}
