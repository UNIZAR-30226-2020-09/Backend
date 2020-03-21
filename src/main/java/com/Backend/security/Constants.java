package com.Backend.security;

public class Constants {

    // UNICAMENTE COLOCAR EN ÉSTA CLASE URL Y STRINGS QUE APAREZCAN EN VARIOS PUNTOS
    // DEL PROGRAMA, EL RESTO EN SUS RESPECTIVAS CLASES
    public static final String LOGIN_USUARIO_URL = "/api/usuarios/login";
    public static final String REGISTRO_USUARIO_URL = "/api/usuarios/registro";
    public static final String CONSULTAR_TODOS_USUARIOS_URL = "/api/usuarios/consultarTodos";
    public static final String CONTACTO_URL = "/api/mensaje";

    //Recomendable dejarlo así, según el estándar
    public static final String HEADER_AUTHORIZATION_KEY = "Authorization";
    public static final String TOKEN_BEARER_PREFIX = "Bearer ";

    public static final long TOKEN_EXPIRATION_TIME = 600L * 1000L; // 10 minutos
    public static final String SUPER_SECRET_KEY = "ClaveSuperUltraMegaSecretisima";
}
