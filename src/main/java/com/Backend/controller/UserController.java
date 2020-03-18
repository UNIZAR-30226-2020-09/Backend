package com.Backend.controller;

import com.Backend.exception.UserNotFoundByMailException;
import com.Backend.exception.UserNotFoundException;
import com.Backend.model.User;
import com.Backend.model.request.UserRegisterRequest;
import com.Backend.repository.IUserRepo;
import com.Backend.security.Constants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.Backend.security.Constants.*;

@RestController
public class UserController {

    @Autowired
    IUserRepo repo;

    static private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /*
     * Ejemplo de inserción de usuario, se recuerda que todos los atributos son necesarios.
     * Habrá que personalizar los atributos dandoles una longitud etc...
     * Cuando no todos los atributos sean obligatorios añadir required = false junto al name.
     * se puede hacer que devuelva algún JSON que confirme o deniegue la correcta inserción
     */
    @PostMapping(REGISTRO_URL)
    public ResponseEntity<String> registro (@RequestBody UserRegisterRequest userRegReq) {
        if (!userRegReq.isValid()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Faltan campos.");
        }
        else {
            if (!repo.existsByMail(userRegReq.getMail())) {
                userRegReq.setMasterPassword(encoder.encode(userRegReq.getMail()));
                repo.save(userRegReq.getAsUser());
                return ResponseEntity.status(HttpStatus.OK).body("El usuario ha sido insertado.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El email ya está asociado a una cuenta.");
            }
        }
    }

    /*
     * Falta adaptar los códigos de respuesta
     */
    @PostMapping(LOGIN_URL)
    public ResponseEntity<User> login(@RequestBody UserRegisterRequest userRegReq) throws UserNotFoundByMailException{
        if(userRegReq.isValid()) {
            User recuperado = repo.findByMail(userRegReq.getMail())
                    .orElseThrow(() -> new UserNotFoundByMailException(userRegReq.getMail()));
            //System.out.println(recuperado.getMasterPassword());
            //System.out.println(userRegReq.getMasterPassword());
            //if(encoder.matches(userRegReq.getMasterPassword(),recuperado.getMasterPassword())) {
                String token = getJWTToken(userRegReq.getMail());
                System.out.println(token);
                User user = new User();
                //user.setMasterPassword(userRegReq.getMasterPassword());
                user.setToken(token);
                return ResponseEntity.status(HttpStatus.OK).body(user);
            //} else {
            //    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userRegReq.getAsUser());
            //}
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userRegReq.getAsUser());
        }
    }

    private String getJWTToken( String username) {
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");

        String token = Jwts
                .builder()
                .setId(UUID.randomUUID().toString()) //Random ID
                .setSubject(username)
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

    /*
     * Devuelve si existe un JSON con la info del usuario, en caso contrario lanza excepcion
     */
    @GetMapping("api/users/consultar/{id}")
    public User consulta(@PathVariable Long id) throws UserNotFoundException {
        User usuario = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return usuario;
    }

    /*
     * Devuelve la información de todos los usuarios de la base de datos
     */
    @GetMapping(LISTAR_TODOS_URL)
    public List<User> all() {
        return repo.findAll();
    }


    /*
     * Método para eliminar usuarios, en proceso de debug, deberíamos usar DELETE
     */
    @DeleteMapping("api/users/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) throws UserNotFoundException {
        User usuario = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        repo.deleteById(usuario.getId());
        return ResponseEntity.status(HttpStatus.OK).body("El usuario ha sido eliminado");
    }
}
