package com.Backend.security;

public class Constants {
    public static final String LOGIN_URL = "/api/users/login";
    public static final String REGISTRO_URL = "/api/users/registro";
    public static final String LISTAR_TODOS_URL = "/api/users/consultar";

    //Recomendable dejarlo así, según el estándar
    public static final String HEADER_AUTHORIZATION_KEY = "Authorization";
    public static final String TOKEN_BEARER_PREFIX = "Bearer";

    public static final long TOKEN_EXPIRATION_TIME = 3600L * 1000L;
    public static final String ISSUER_INFO = "";
    public static final String SUPER_SECRET_KEY = "ClaveSuperUltraMegaSecretisima";
}
