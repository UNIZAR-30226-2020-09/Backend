package com.Backend.controller;

import com.Backend.model.request.MessageRequest;
import com.Backend.repository.IMensajeRepo;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.Backend.security.Constants.*;

@RestController
public class MensajeController {

    @Autowired
    IMensajeRepo repo;

    @PostMapping(CONTACTO_URL)
    public ResponseEntity<JSONObject> contactar(@RequestBody MessageRequest msgReq){
        JSONObject res = new JSONObject();

        if(msgReq.isValid()){
            repo.save(msgReq.getAsMessage());
            return ResponseEntity.status(HttpStatus.OK).body(res);
        } else {
            res.put("statusText", "BAD_REQUEST, Error al guardar el mensaje.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
    }
}
