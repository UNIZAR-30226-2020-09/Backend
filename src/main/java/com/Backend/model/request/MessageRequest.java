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
        return mail!=null && body!=null;
    }

    public Message getAsMessage(){
        return new Message(mail, body);
    }
}
