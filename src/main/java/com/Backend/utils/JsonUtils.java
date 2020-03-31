package com.Backend.utils;

import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class JsonUtils{

    public static ResponseEntity<JSONObject> peticionErronea(String msg){
        JSONObject res = new JSONObject();
        res.put("statusText", msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    public static ResponseEntity<JSONObject> peticionCorrecta(){
        JSONObject res = new JSONObject();
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
