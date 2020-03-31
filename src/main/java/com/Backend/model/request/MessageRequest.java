package com.Backend.model.request;

import com.Backend.model.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {

    @Getter
    private String mail;
    @Getter
    private String body;

    public boolean isValid(){
        return !mail.isEmpty() && !body.isEmpty();
    }

    public Message getAsMessage(){
        return new Message(mail, body);
    }
}
