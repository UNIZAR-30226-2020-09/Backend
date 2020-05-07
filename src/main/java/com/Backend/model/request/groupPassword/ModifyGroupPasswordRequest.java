package com.Backend.model.request.groupPassword;

import com.Backend.model.Password;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.util.LinkedList;

import static com.Backend.security.SecurityConstants.SUPER_SECRET_KEY;

@AllArgsConstructor
public class ModifyGroupPasswordRequest {

    @Getter
    @Setter
    private Long passId;

    @Getter
    @Setter
    private String passwordName;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String optionalText;

    @Getter
    @Setter
    private String userName;

    @Getter
    @Setter
    Integer expirationTime;

    @Getter
    @Setter
    LinkedList<String> usuarios;

    public boolean isValid(){
        return  passId!=null && (
                 (passwordName!=null && !passwordName.isEmpty()) ||
                 (password!=null && !password.isEmpty()) ||
                 (optionalText!=null && !optionalText.isEmpty()) ||
                 (userName!=null && !userName.isEmpty()) ||
                 expirationTime!=null ||
                 usuarios != null
        );
    }
}
