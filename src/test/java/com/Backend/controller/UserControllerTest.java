package com.Backend.controller;

import com.Backend.BackendApplication;
import com.Backend.model.request.UserRegisterRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.LinkedHashMap;

import static com.Backend.security.Constants.LOGIN_USUARIO_URL;
import static com.Backend.security.Constants.REGISTRO_USUARIO_URL;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

    private TestRestTemplate restTemplate = new TestRestTemplate();

    private String url = "http://localhost:8080";

    static HttpHeaders basicHeaders;
    static UserRegisterRequest user1;
    static UserRegisterRequest user2;
    static UserRegisterRequest user3;
    static UserRegisterRequest incompleteUserReq;
    static String user1Password;
    static String tokenUser2; // Asignado en la petición delete fallida

    @BeforeAll
    static void preparacion(){
        basicHeaders = new HttpHeaders();
        basicHeaders.setContentType(MediaType.APPLICATION_JSON);
        user1 = new UserRegisterRequest("user1@test.com","Usuario1");
        user2 = new UserRegisterRequest("user2@test.com","Usuario2");
        user3 = new UserRegisterRequest("user3@test.com","Usuario3");
        incompleteUserReq = new UserRegisterRequest();
        user1Password = user1.getMasterPassword();
    }

    @Test
    @Order(1)
    void post_registro_OK() throws JsonProcessingException {

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(user1), basicHeaders);
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url + REGISTRO_USUARIO_URL, entity, JSONObject.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(2)
    // Mail ya ocupado, usuario insertado
    void post_registro_OK_followed_by_BAD_REQUEST_1() throws JsonProcessingException {

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(user2), basicHeaders);
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url + REGISTRO_USUARIO_URL, entity, JSONObject.class);
        HttpEntity<String> sameMailEntity = new HttpEntity<>(new ObjectMapper().writeValueAsString(user2), basicHeaders);
        ResponseEntity<JSONObject> sameMailResponse = restTemplate.postForEntity(url + REGISTRO_USUARIO_URL, sameMailEntity, JSONObject.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, sameMailResponse.getStatusCode());
        assertNotEquals(sameMailResponse.getStatusCode(), response.getStatusCode());
    }

    @Test
    @Order(3)
    // Faltan campos
    void post_registro_BAD_REQUEST_2() throws JsonProcessingException {

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(incompleteUserReq), basicHeaders);
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url + REGISTRO_USUARIO_URL, entity, JSONObject.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(4)
    void post_login_OK() throws JsonProcessingException {

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(user1), basicHeaders);
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url + LOGIN_USUARIO_URL, entity, JSONObject.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(5)
    // Faltan campos
    void post_login_BAD_REQUEST_1() throws JsonProcessingException {

        user1.setMasterPassword(null);
        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(user1), basicHeaders);
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url + LOGIN_USUARIO_URL, entity, JSONObject.class);
        user1.setMasterPassword(user1Password);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(6)
    // No existe usuario con ese mail
    void post_login_BAD_REQUEST_2() throws JsonProcessingException {

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(user3), basicHeaders);
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url + LOGIN_USUARIO_URL, entity, JSONObject.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(7)
    // No coincide la contraseña
    void post_login_BAD_REQUEST_3() throws JsonProcessingException {

        user1.setMasterPassword("wrongPass");
        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(user1), basicHeaders);
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url + LOGIN_USUARIO_URL, entity, JSONObject.class);
        user1.setMasterPassword(user1Password);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

    @Test
    @Order(8)
    void delete_eliminar_OK() throws JsonProcessingException {

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(user1), basicHeaders);
        ResponseEntity<JSONObject> response = restTemplate.exchange(URI.create(url + LOGIN_USUARIO_URL),
                    HttpMethod.POST,entity, JSONObject.class);

        String token = response.getBody().getAsString("token");

        HttpHeaders headersUser1 = headerFromToken(token);

        response = restTemplate.exchange(URI.create(url + "/api/usuarios/eliminar"),
                HttpMethod.DELETE, new HttpEntity<>(headersUser1), JSONObject.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(9)
    // Prueba con token alterado
    void delete_eliminar_NOT_OK_1() throws JsonProcessingException {

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(user2), basicHeaders);
        ResponseEntity<JSONObject> response = restTemplate.exchange(URI.create(url + LOGIN_USUARIO_URL),
                HttpMethod.POST, entity, JSONObject.class);

        tokenUser2 = response.getBody().getAsString("token"); //Elimina el "Bearer " al inicio del token
        String token = tokenUser2 + "error";
        HttpHeaders headersUser2 = headerFromToken(token);

        response = restTemplate.exchange(URI.create(url + "/api/usuarios/eliminar"),
                HttpMethod.DELETE, new HttpEntity<>(headersUser2), JSONObject.class);

        assertNotEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(10)
    void get_consulta_OK() {

        HttpHeaders headersUser2 = headerFromToken(tokenUser2);
        ResponseEntity<JSONObject> response = restTemplate.exchange(URI.create(url + "/api/usuarios/consultar"),
                HttpMethod.GET, new HttpEntity<>(headersUser2), JSONObject.class);

        JSONObject user = new JSONObject((LinkedHashMap<String, String>)response.getBody().get("user"));
        assertEquals(user.getAsString("mail"),"user2@test.com");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(11)
    // Token no autorizado
    void get_consulta_NOT_OK_1() {

        String token = tokenUser2 + "modified";
        HttpHeaders headersUser2 = headerFromToken(token);

        ResponseEntity<JSONObject> response = restTemplate.exchange(URI.create(url + "/api/usuarios/consultar"),
                HttpMethod.GET, new HttpEntity<>(headersUser2), JSONObject.class);

        assertNotEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(12)
    //Deja la bbdd en su estado original
    void delete_eliminar_OK_2() throws JsonProcessingException {

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(user2), basicHeaders);
        ResponseEntity<JSONObject> response = restTemplate.exchange(URI.create(url + LOGIN_USUARIO_URL),
                HttpMethod.POST,entity, JSONObject.class);

        String token = response.getBody().getAsString("token");
        HttpHeaders headersUser2 = headerFromToken(token);

        response = restTemplate.exchange(URI.create(url + "/api/usuarios/eliminar"),
                HttpMethod.DELETE, new HttpEntity<>(headersUser2), JSONObject.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // Genera la cabecera a partir de un token devuelto por PandoraApp
    HttpHeaders headerFromToken(String token){
        HttpHeaders headersUser = new HttpHeaders();
        headersUser.setContentType(MediaType.APPLICATION_JSON);
        headersUser.setBearerAuth(token.substring(7)); // Elimina la subcadena "Bearer " al inicio del token
        return headersUser;
    }

}