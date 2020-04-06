package com.Backend.controller;

import com.Backend.repository.ICatRepo;
import com.Backend.repository.IMensajeRepo;
import com.Backend.repository.IPassRepo;
import com.Backend.repository.IUserRepo;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.Backend.security.Constants.ESTADISTICAS;

@RestController
public class PandoraController {

    @Autowired
    IUserRepo repoUser;
    @Autowired
    ICatRepo repoCat;
    @Autowired
    IPassRepo repoPass;
    @Autowired
    IMensajeRepo repoMsg;

    @GetMapping(ESTADISTICAS)
    public ResponseEntity<JSONObject> stats(HttpServletRequest request) {

        int numUsers = repoUser.findAll().size();
        int numCat = repoCat.findAll().size();
        int numPass = repoPass.findAll().size();
        int numMsgs = repoMsg.findAll().size();
        JSONObject res = new JSONObject();
        res.put("nUsuarios", numUsers);
        res.put("nContraseñas", numPass);
        res.put("nCat", numCat);
        res.put("nMsgs", numMsgs);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

}
