package com.Backend.model.request;

import lombok.Getter;
import lombok.Setter;

public class ModifyUserRequest {

    @Getter @Setter
    private String oldMasterPassword;
    @Getter @Setter
    private String newMasterPassword;
    @Getter @Setter
    private String mail;

    public boolean isValid(){
        return  (mail !=null && !mail.isEmpty()) ||
                (oldMasterPassword != null && !oldMasterPassword.isEmpty()) ||
                (newMasterPassword != null && !newMasterPassword.isEmpty());
    }

}
