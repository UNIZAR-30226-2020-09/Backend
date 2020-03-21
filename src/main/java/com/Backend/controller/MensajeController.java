package com.Backend.controller;

import com.Backend.model.request.MessageRequest;
import com.Backend.repository.IMensajeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.Backend.security.Constants.*;

@RestController
public class MensajeController {

    @Autowired
    IMensajeRepo repo;

    /*
     * Operación que recibe los formularios de contacto y los inserta en la base de datos.
     */
    @CrossOrigin
    @PostMapping(CONTACTO_URL)
    public ResponseEntity<String> contactar(@RequestBody MessageRequest msgReq){
        if(msgReq.isValid()){
            repo.save(msgReq.getAsMessage());
            return ResponseEntity.status(HttpStatus.OK).body("Mensaje insertado correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al guardar el mensaje.");
        }
    }
}
