package com.Backend.model.request.password;

import com.Backend.model.Password;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@AllArgsConstructor
public class InsertPasswordRequest {

    @Getter @Setter
    private String masterPassword;
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
        return masterPassword!=null && !masterPassword.isEmpty() &&
                password!=null && !password.isEmpty() &&
                passwordName!=null && !passwordName.isEmpty() &&
                expirationTime != null && passwordCategoryId != null &&
                expirationTime > 0;
    }

    public Password getAsPassword() {
        TextEncryptor textEncryptor = Encryptors.text(masterPassword, "46b930");
        Password pwd = new Password(textEncryptor.encrypt(password), passwordName, expirationTime);
        pwd.setOptionalText(optionalText);
        pwd.setUserName(userName);
        return pwd;
    }
}
