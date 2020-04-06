package com.Backend.model.request;

import lombok.Getter;
import lombok.Setter;

public class GeneratePasswordRequest {

    @Getter @Setter
    private Boolean minus;
    @Getter @Setter
    private Boolean mayus;
    @Getter @Setter
    private Boolean numbers;
    @Getter @Setter
    private Boolean specialCharacters;
    @Getter @Setter
    private Integer length;

    public boolean isValid(){
        return length != null && minus != null && mayus != null && numbers != null && specialCharacters != null
                && length > 0 && (minus || mayus || numbers || specialCharacters);
    }
}
