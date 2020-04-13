package com.Backend.model.request;

import com.Backend.model.Password;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class ModifyPasswordRequest {

    @Getter @Setter
    private String masterPassword;
    @Getter @Setter
    private Long id;
    @Getter @Setter
    private String passwordName;
    @Getter @Setter
    private Long passwordCategoryId;
    @Getter @Setter
    private String password;
    @Getter @Setter
    private String optionalText;
    @Getter @Setter
    private String userName;
    @Getter @Setter
    Integer expirationTime;

    public boolean isValid(){
        return id != null && masterPassword!=null && !masterPassword.isEmpty() &&
                ((password!=null && !password.isEmpty()) ||
                (passwordName!=null && !passwordName.isEmpty()) ||
                expirationTime != null || passwordCategoryId != null ||
                (optionalText != null && !optionalText.isEmpty()) ||
                (userName != null && !userName.isEmpty()) );
    }
}
