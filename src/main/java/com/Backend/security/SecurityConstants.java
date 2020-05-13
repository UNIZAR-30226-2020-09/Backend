package com.Backend.security;

public class SecurityConstants {

    // UNICAMENTE COLOCAR EN ÉSTA CLASE URL Y STRINGS QUE APAREZCAN EN VARIOS PUNTOS
    // DEL PROGRAMA, EL RESTO EN SUS RESPECTIVAS CLASES
    public static final String LOGIN_USUARIO_URL = "/api/usuarios/login";
    public static final String LOGIN_2FA_URL = "/api/2FA/login";
    public static final String RECUPERAR_USUARIO_URL = "/api/2FA/recuperar";
    public static final String VERIFICAR_RESET_USUARIO_URL = "/api/2FA/verificarReset";
    public static final String LOGIN_USUARIO2FA_URL = "/api/usuarios/loginCon2FA";
    public static final String VERIFICAR_USUARIO_URL = "/api/usuarios/verificar";
    public static final String REGISTRO_USUARIO_URL = "/api/usuarios/registro";
    public static final String CONSULTAR_TODOS_USUARIOS_URL = "/api/usuarios/consultarTodos";
    public static final String CONTACTO_URL = "/api/mensaje";
    public static final String ESTADISTICAS = "/api/estadisticas";
    public static final String ROBUSTEZ = "/api/robustez";

    //Recomendable dejarlo así, según el estándar
    public static final String HEADER_AUTHORIZATION_KEY = "Authorization";
    public static final String TOKEN_BEARER_PREFIX = "Bearer ";

    public static final long TOKEN_EXPIRATION_TIME = 600000000000L * 60L;
    public static final String SUPER_SECRET_KEY = "ClaveSuperUltraMegaSecretisima";
}
