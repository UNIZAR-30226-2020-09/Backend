package com.Backend.controller;

import com.Backend.model.Category;
import com.Backend.model.request.DeleteByIdRequest;
import com.Backend.model.request.InsertCategoryRequest;
import com.Backend.model.request.ModifyCategoryRequest;
import com.Backend.model.request.UserRegisterRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;

import static com.Backend.security.Constants.LOGIN_USUARIO_URL;
import static com.Backend.security.Constants.REGISTRO_USUARIO_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoryControllerTest {

    public static final String INSERTAR_CATEGORIA_URL = "/api/categorias/insertar";
    public static final String ELIMINAR_CATEGORIA_URL = "/api/categorias/eliminar";
    public static final String LISTAR_CATEGORIAS_USUARIO_URL = "/api/categorias/listar";
    public static final String MODIFICAR_CATEGORIAS_USUARIO_URL = "/api/categorias/modificar";

    private static UserRegisterRequest user1;
    static HttpHeaders basicHeaders;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    static InsertCategoryRequest cat1;
    static InsertCategoryRequest cat2;
    static InsertCategoryRequest cat3;
    static InsertCategoryRequest emptyCat;

    static DeleteByIdRequest cat1Id;
    static DeleteByIdRequest cat2Id;

    static ModifyCategoryRequest modcat2;

    private String url = "http://localhost:8080";
    static HttpHeaders tokenHeaders;

    static String token;

    @BeforeAll
    static void preparacion() {
        basicHeaders = new HttpHeaders();
        basicHeaders.setContentType(MediaType.APPLICATION_JSON);
        user1 = new UserRegisterRequest("user1@test.com","Usuario1");

        token = "";
        cat1 = new InsertCategoryRequest("categ1");
        cat2 = new InsertCategoryRequest("categ2");
        cat3 = new InsertCategoryRequest("categ3");
        emptyCat = new InsertCategoryRequest("");
    }

    @AfterAll
    public static void tearDown() {
        TestRestTemplate restTemplateTearDown = new TestRestTemplate();
        String url = "http://localhost:8080";
        restTemplateTearDown.exchange(URI.create(url + "/api/usuarios/eliminar"),
                HttpMethod.DELETE, new HttpEntity<>(tokenHeaders), JSONObject.class);
    }

    @Test
    @Order(1)
    void get_sinCategoria_OK() throws JsonProcessingException {
        create_user_and_login();

        ResponseEntity<JSONObject> response = restTemplate.exchange(URI.create(url + LISTAR_CATEGORIAS_USUARIO_URL),
                HttpMethod.GET, new HttpEntity<>(tokenHeaders), JSONObject.class);
        ArrayList<Category> res = (ArrayList<Category>) response.getBody().get("categories");
        JSONObject cat = new JSONObject((Map<String, ?>) res.get(0));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Sin categoría", cat.get("categoryName"));
    }

    @Test
    @Order(2)
    void post_insert_OK() throws JsonProcessingException {
        create_user_and_login();

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(cat1), tokenHeaders);
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url + INSERTAR_CATEGORIA_URL, entity, JSONObject.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(3)
    void post_insert_OK_followed_by_BAD_REQUEST() throws JsonProcessingException {
        create_user_and_login();

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(cat2), tokenHeaders);
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url + INSERTAR_CATEGORIA_URL, entity, JSONObject.class);
        HttpEntity<String> sameMailEntity = new HttpEntity<>(new ObjectMapper().writeValueAsString(cat2), tokenHeaders);
        ResponseEntity<JSONObject> sameMailResponse = restTemplate.postForEntity(url + INSERTAR_CATEGORIA_URL, sameMailEntity, JSONObject.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, sameMailResponse.getStatusCode());
        assertNotEquals(sameMailResponse.getStatusCode(), response.getStatusCode());
    }


    @Test
    @Order(4)
    void post_registro_BAD_REQUEST_2() throws JsonProcessingException {
        create_user_and_login();

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(emptyCat), tokenHeaders);
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url + INSERTAR_CATEGORIA_URL, entity, JSONObject.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(5)
    void get_listar_OK() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        create_user_and_login();

        JSONObject cat1 = get_category(1);
        JSONObject cat2 = get_category(2);
        assertEquals("categ1", cat1.get("categoryName"));
        assertEquals("categ2", cat2.get("categoryName"));
    }

    @Test
    @Order(6)
    void post_modificar_OK() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        create_user_and_login();

        JSONObject cat2 = get_category(2);

        modcat2 = new ModifyCategoryRequest(((Number)cat2.get("catId")).longValue(), "categ2MOD");
        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(modcat2), tokenHeaders);
        ResponseEntity<JSONObject> response = restTemplate.postForEntity(url + MODIFICAR_CATEGORIAS_USUARIO_URL, entity, JSONObject.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        cat2 = get_category(2);

        assertEquals("categ2MOD", cat2.get("categoryName"));
    }

    @Test
    @Order(7)
    void delete_eliminar_OK_BAD() throws JsonProcessingException {
        create_user_and_login();

        JSONObject cat1 = get_category(1);

        cat1Id = new DeleteByIdRequest(((Number)cat1.get("catId")).longValue());

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(cat1Id), tokenHeaders);
        ResponseEntity<JSONObject> response = restTemplate.exchange(URI.create(url + ELIMINAR_CATEGORIA_URL),
                HttpMethod.DELETE, entity, JSONObject.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        //Intentamos borrar por segunda vez
        response = restTemplate.exchange(URI.create(url + ELIMINAR_CATEGORIA_URL),
                HttpMethod.DELETE, entity, JSONObject.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    void create_user_and_login() throws JsonProcessingException {
        if(token == "") {
            HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(user1), basicHeaders);
            ResponseEntity<JSONObject> response = restTemplate.postForEntity(url + REGISTRO_USUARIO_URL, entity, JSONObject.class);

            entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(user1), basicHeaders);
            response = restTemplate.postForEntity(url + LOGIN_USUARIO_URL, entity, JSONObject.class);
            token = response.getBody().getAsString("token");
            tokenHeaders = headerFromToken(token);
        }
    }

    JSONObject get_category(int i){
        ResponseEntity<JSONObject> response = restTemplate.exchange(URI.create(url + LISTAR_CATEGORIAS_USUARIO_URL),
                HttpMethod.GET, new HttpEntity<>(tokenHeaders), JSONObject.class);
        ArrayList<Category> res = (ArrayList<Category>) response.getBody().get("categories");
        return new JSONObject((Map<String, ?>) res.get(i));
    }

    void remove_user() throws JsonProcessingException {
        restTemplate.exchange(URI.create(url + "/api/usuarios/eliminar"),
                HttpMethod.DELETE, new HttpEntity<>(tokenHeaders), JSONObject.class);
    }

    // Genera la cabecera a partir de un token devuelto por PandoraApp
    HttpHeaders headerFromToken(String token){
        HttpHeaders headersUser = new HttpHeaders();
        headersUser.setContentType(MediaType.APPLICATION_JSON);
        headersUser.setBearerAuth(token.substring(7)); // Elimina la subcadena "Bearer " al inicio del token
        return headersUser;
    }

}