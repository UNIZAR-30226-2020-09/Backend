package com.Backend.security;

public class Constants {

    public static final String LOGIN_USUARIO_URL = "/api/users/login";
    public static final String LOGOUT_USUARIO_URL = "api/users/logout";
    public static final String REGISTRO_USUARIO_URL = "/api/users/registroUser";
    public static final String CONSULTAR_TODOS_USUARIOS_URL = "/api/users/consultarTodosUsuarios";
    public static final String CONSULTAR_USUARIO_URL =  "/api/users/consultarUsuario";
    public static final String ELIMINAR_USUARIO_URL = "/api/users/eliminarUsuario";

    //Recomendable dejarlo así, según el estándar
    public static final String HEADER_AUTHORIZATION_KEY = "Authorization";
    public static final String TOKEN_BEARER_PREFIX = "Bearer ";

    public static final long TOKEN_EXPIRATION_TIME = 600L * 1000L; // 10 minutos
    public static final String ISSUER_INFO = "";
    public static final String SUPER_SECRET_KEY = "ClaveSuperUltraMegaSecretisima";
}
