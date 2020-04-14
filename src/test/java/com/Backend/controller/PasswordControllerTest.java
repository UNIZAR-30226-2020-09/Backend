package com.Backend.controller;


import com.Backend.model.Category;
import com.Backend.model.User;
import com.Backend.model.request.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.ArrayList;
import java.util.Map;

import static com.Backend.controller.CategoryController.INSERTAR_CATEGORIA_URL;
import static com.Backend.controller.CategoryController.LISTAR_CATEGORIAS_USUARIO_URL;
import static com.Backend.controller.PasswordController.*;
import static com.Backend.security.Constants.LOGIN_USUARIO_URL;
import static com.Backend.security.Constants.REGISTRO_USUARIO_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PasswordControllerTest {
/*
    private static TestRestTemplate restTemplate = new TestRestTemplate();
    private static String url = "http://localhost:8080";
    static HttpHeaders basicHeaders;
    static HttpHeaders headersUser1;
    static HttpHeaders headersUser2;
    static HttpHeaders headersUser3;
    static UserRegisterRequest user1;
    static UserRegisterRequest user2;
    static UserRegisterRequest user3;
    static Long sinCategoriaIdUser1;
    static Long sinCategoriaIdUser2;
    static Long sinCategoriaIdUser3;
    static InsertPasswordRequest ipr1;
    static InsertPasswordRequest ipr2;
    static InsertPasswordRequest ipr3;
    static ArrayList<Long> passwordsOfUser;

    @BeforeAll
    static void preparacion() throws JsonProcessingException{

        basicHeaders = new HttpHeaders();
        basicHeaders.setContentType(MediaType.APPLICATION_JSON);
        user1 = new UserRegisterRequest("user1@test.com","Usuario1");
        user2 = new UserRegisterRequest("user2@test.com","Usuario2");
        user3 = new UserRegisterRequest("user3@test.com","Usuario3");

        String token = registroLoginUsuario(user1);
        headersUser1 = headerFromToken(token);
        token = registroLoginUsuario(user2);
        headersUser2 = headerFromToken(token);
        token = registroLoginUsuario(user3);
        headersUser3 = headerFromToken(token);

        sinCategoriaIdUser1 = sinCategoria(headersUser1);
        sinCategoriaIdUser2 = sinCategoria(headersUser2);
        sinCategoriaIdUser3 = sinCategoria(headersUser3);

        ipr1 = new InsertPasswordRequest("name1", sinCategoriaIdUser1,
                "pass1","oT", null, 300);
        ipr2 = new InsertPasswordRequest("name2", sinCategoriaIdUser2,
                "pass2","jeje", "@user", 90);
        ipr3 = new InsertPasswordRequest("name3", sinCategoriaIdUser3,
                "pass3","test", null, 10);
    }

    @AfterAll
    static void limpiar(){
        eliminarUsuario(headersUser1);
        eliminarUsuario(headersUser2);
        eliminarUsuario(headersUser3);
    }

    @Test
    @Order(1)
    void post_password_OK_before_BAD_REQUEST() throws JsonProcessingException {

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(ipr1), headersUser1);
        ResponseEntity<JSONObject> response = restTemplate.exchange(URI.create(url + INSERTAR_PASSWORD_URL),
                HttpMethod.POST, entity, JSONObject.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = restTemplate.exchange(URI.create(url + INSERTAR_PASSWORD_URL),
                HttpMethod.POST, entity, JSONObject.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(2)
    void post_password_BAD_REQUEST_before_3_OKs() throws JsonProcessingException {
        //Password mismo nombre
        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(ipr1), headersUser1);
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url + INSERTAR_PASSWORD_URL, entity, JSONObject.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //Nueva password
        entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(ipr2), headersUser1);
        response = restTemplate.postForEntity(url + INSERTAR_PASSWORD_URL, entity, JSONObject.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        //Nueva password
        entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(ipr3), headersUser1);
        response = restTemplate.postForEntity(url + INSERTAR_PASSWORD_URL, entity, JSONObject.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        //Password para otro usuario
        entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(ipr2), headersUser2);
        response = restTemplate.postForEntity(url + INSERTAR_PASSWORD_URL, entity, JSONObject.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(3)
    void listar_OK() {

        ResponseEntity<JSONObject> response = restTemplate.exchange(URI.create(url + LISTAR_PASSWORDS_USUARIO_URL),
                HttpMethod.GET, new HttpEntity<String>(headersUser1), JSONObject.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        cogerIdsPasswords(response); // Para próximos tests

       response = restTemplate.exchange(URI.create(url + LISTAR_PASSWORDS_USUARIO_URL),
                HttpMethod.GET, new HttpEntity<String>(headersUser2), JSONObject.class);
       assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(4)
    void generar_password_OKs() throws JsonProcessingException {

        GeneratePasswordRequest genPassReq1 = new GeneratePasswordRequest(false,true,true,true,12);
        GeneratePasswordRequest genPassReq2 = new GeneratePasswordRequest(true,false,true,true,17);
        GeneratePasswordRequest genPassReq3 = new GeneratePasswordRequest(true,true,false,true,22);

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(genPassReq1), headersUser1);
        ResponseEntity<JSONObject> response = restTemplate.exchange(URI.create(url + GENERAR_PASSWORD_URL),
                HttpMethod.POST, entity, JSONObject.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(genPassReq2), headersUser1);
        response = restTemplate.exchange(URI.create(url + GENERAR_PASSWORD_URL),
                HttpMethod.POST, entity, JSONObject.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(genPassReq3), headersUser1);
        response = restTemplate.exchange(URI.create(url + GENERAR_PASSWORD_URL),
                HttpMethod.POST, entity, JSONObject.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(5)
    void modificar_password() throws JsonProcessingException {
        ModifyPasswordRequest modPassReq1 = new ModifyPasswordRequest(passwordsOfUser.get(0), "name",
                sinCategoriaIdUser1, "pass", "optional", null, 99);

        ModifyPasswordRequest modPassReq2 = new ModifyPasswordRequest(passwordsOfUser.get(0), "name",
                -1L, "pass", "optional", null, 99);

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(modPassReq1), headersUser1);
        ResponseEntity<JSONObject> response = restTemplate.exchange(URI.create(url + MODIFICAR_PASSWORDS_USUARIO_URL),
                HttpMethod.POST, entity, JSONObject.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(modPassReq2), headersUser1);
        response = restTemplate.exchange(URI.create(url + MODIFICAR_PASSWORDS_USUARIO_URL),
                HttpMethod.POST, entity, JSONObject.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(6)
    void eliminar_password_OKs() throws JsonProcessingException {

        for(Long l : passwordsOfUser) {
            DeleteByIdRequest dbIdReq1 = new DeleteByIdRequest(l);
            HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(dbIdReq1), headersUser1);
            ResponseEntity<JSONObject> response = restTemplate.exchange(URI.create(url + ELIMINAR_PASSWORD_URL),
                    HttpMethod.DELETE, entity, JSONObject.class);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    @Order(7)
    void eliminar_password_BAD_REQUEST() throws JsonProcessingException {

        DeleteByIdRequest dbIdReq1 = new DeleteByIdRequest(passwordsOfUser.get(0));
        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(dbIdReq1), headersUser1);
        ResponseEntity<JSONObject> response = restTemplate.exchange(URI.create(url + ELIMINAR_PASSWORD_URL),
                HttpMethod.DELETE, entity, JSONObject.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Genera la cabecera a partir de un token devuelto por PandoraApp
    static HttpHeaders headerFromToken(String token){
        HttpHeaders headersUser = new HttpHeaders();
        headersUser.setContentType(MediaType.APPLICATION_JSON);
        headersUser.setBearerAuth(token.substring(7)); // Elimina la subcadena "Bearer " al inicio del token
        return headersUser;
    }

    // Devuelve el token del usuario que ha hecho el registro y el login
    static String registroLoginUsuario(UserRegisterRequest usuario) throws JsonProcessingException{
        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(usuario), basicHeaders);
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url + REGISTRO_USUARIO_URL, entity, JSONObject.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(usuario), basicHeaders);
        response = restTemplate.postForEntity(url + LOGIN_USUARIO_URL, entity, JSONObject.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        return response.getBody().getAsString("token");
    }

    static void cogerIdsPasswords(ResponseEntity<JSONObject> response){
        ArrayList<JSONObject> passwordList = (ArrayList<JSONObject>) response.getBody().get("passwords");
        passwordsOfUser = new ArrayList<>();
        for(int i = 0; i < passwordList.size(); ++i){
            JSONObject pass =  new JSONObject(passwordList.get(i));
            Long passwordId = Long.parseLong(pass.getAsString("passId"));
            passwordsOfUser.add(passwordId);
        }
    }

    //Devuelve la categoría sin categoría del usuario
    static Long sinCategoria(HttpHeaders header) {

        ResponseEntity<JSONObject> response = restTemplate.exchange(URI.create(url + LISTAR_CATEGORIAS_USUARIO_URL),
                HttpMethod.GET, new HttpEntity<>(header), JSONObject.class);
        ArrayList<JSONObject> res = (ArrayList<JSONObject>) response.getBody().get("categories");
        JSONObject cat =  new JSONObject( res.get(0));
        String categoryName = cat.getAsString("categoryName");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Sin categoría", categoryName);
        return Long.parseLong(cat.getAsString("catId"));
    }

    //Envía petición de eliminar usuario
    static void eliminarUsuario(HttpHeaders headersUsuario){
        ResponseEntity<JSONObject> response = restTemplate.exchange(URI.create(url + "/api/usuarios/eliminar"),
                HttpMethod.DELETE, new HttpEntity<>(headersUsuario), JSONObject.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }*/
}
