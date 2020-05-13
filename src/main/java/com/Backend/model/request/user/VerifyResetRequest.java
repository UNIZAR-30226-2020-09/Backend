package com.Backend.model.request.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class VerifyResetRequest {

    @Getter
    @Setter
    private String mail;

    @Getter
    @Setter
    private String oldMasterPassword;

    @Getter
    @Setter
    private String newMasterPassword;


    @Getter
    @Setter
    private String resetCode;

    public boolean isValid(){
        return mail != null && !mail.isEmpty() && resetCode!=null && !resetCode.isEmpty() &&
        oldMasterPassword != null && !oldMasterPassword.isEmpty() && newMasterPassword != null && !newMasterPassword.isEmpty();
    }

}
