package com.Backend.utils;

import com.Backend.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.Backend.security.Constants.*;
import static java.lang.Long.parseLong;

public class TokenUtils {

    public static Long getUserFromRequest(HttpServletRequest request){
        final String authorization = request.getHeader("Authorization");
        //El 7 proviene de eliminar la cabecera "Bearer "
        return parseLong(getUserNameFromToken(authorization.substring(7)));
    }

    public static String getJWTToken(User usuario) {
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");

        String token = Jwts
                .builder()
                .setId(UUID.randomUUID().toString()) // El id debe ser único
                .setSubject(usuario.getId().toString()) // A quién pertenece el token
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512,
                        SUPER_SECRET_KEY.getBytes()).compact();

        return TOKEN_BEARER_PREFIX + token;
    }

    public static String getUserNameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public static <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private static Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(SUPER_SECRET_KEY.getBytes()).parseClaimsJws(token).getBody();
    }
}
