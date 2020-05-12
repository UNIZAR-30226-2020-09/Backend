package com.Backend.utils;

import com.Backend.exception.UserNotFoundException;
import com.Backend.model.User;
import com.Backend.repository.IUserRepo;
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

import static com.Backend.security.SecurityConstants.*;

public class TokenUtils {

    public static User getUserFromRequest(HttpServletRequest request, IUserRepo repoUser) throws UserNotFoundException{
        String id = getUserIdFromRequest(request);
        return repoUser.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public static String getUserIdFromRequest(HttpServletRequest request){
        final String authorization = request.getHeader("Authorization");
        //El 7 proviene de eliminar la cabecera "Bearer "
        return getUserIdFromToken(authorization.substring(7));
    }

    public static String getJWTTokenFromUser(User usuario, IUserRepo repo) throws UserNotFoundException {
        User recuperado = repo.findById(usuario.getId()).orElseThrow(() -> new UserNotFoundException(usuario.getId()));
        return getJWTToken(usuario, recuperado.getMasterPassword());
    }

    public static String getJWTToken(User usuario, String hashedPassword ) {

        // Únicamente le autorizamos como usuario
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("Rol_usuario");

        String token = Jwts
                .builder()
                .setId(UUID.randomUUID().toString()) // El id debe ser único
                .setSubject(usuario.getId()) // A quién pertenece el token
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .claim("hash", hashedPassword)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512,
                        SUPER_SECRET_KEY.getBytes()).compact();

        return TOKEN_BEARER_PREFIX + token;
    }

    public static String getUserIdFromToken(String token) {
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
