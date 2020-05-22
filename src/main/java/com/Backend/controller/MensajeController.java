package com.Backend.controller;

import com.Backend.model.request.message.MessageRequest;
import com.Backend.repository.IMensajeRepo;
import com.Backend.utils.SendGridEmailService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.Backend.security.SecurityConstants.CONTACTO_URL;

@RestController
public class MensajeController {

    @Autowired
    IMensajeRepo repo;

    @Autowired
    SendGridEmailService senGridService;

    @PostMapping(CONTACTO_URL)
    public ResponseEntity<JSONObject> contactar(@RequestBody MessageRequest msgReq){
        JSONObject res = new JSONObject();
        if(!msgReq.isValid()){
            res.put("statusText", "Los campos no pueden quedar vacíos.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
        senGridService.sendHTML("pandora.app.unizar@gmail.com", msgReq.getMail(), "Contacto con Pandora", "<h1>Pandora</h1><p>&nbsp;</p><p>Muchas gracias por contactar con nosotros.</p>");
        senGridService.sendText("pandora.app.unizar@gmail.com", "pandora.app.unizar@gmail.com", "Contacto con Pandora", "Mensaje de " +  msgReq.getMail() + ": \n" + msgReq.getBody());
        repo.save(msgReq.getAsMessage());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
