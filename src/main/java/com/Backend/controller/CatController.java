package com.Backend.controller;

import com.Backend.model.Category;
import com.Backend.model.User;
import com.Backend.repository.ICatRepo;
import com.Backend.repository.IUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CatController {

    /*
     * Anota warning "débil", según he leído hacer autowired a un atributo que es una interfaz
     * provoca una implementación en la instanciación de la clase. Inyección de atributos
     */
    @Autowired
    ICatRepo repoCat;

    @Autowired
    IUserRepo repoUser;

    /*
     * Why CrossOrigin: https://www.arquitecturajava.com/spring-rest-cors-y-su-configuracion/
     * Este método simula la inserción de una categoría en la base de datos. No se había marcado anteriormente,
     * por tanto, se ha añadido que toda categoría deba tener un usuario, en este caso es solo un ejemplo y lo hago con
     * el mail. Encuentro el usuario e inserto la categoría, si falla da excepciones, que deberían personalizarse.
     *
     * Horrible el método, más adelante haré que devuelva los JSON etc.. de momento el UserController
     * creo que refleja mucho mejor el cómo creo que deberíamos hacerlo para evitar hardcoding en el frontend.
     */
    @CrossOrigin
    @RequestMapping("/insertarCat")
    public String insertarCat ( @RequestParam("name") String name, @RequestParam("mail") String mail) {
        try {
            User propietario = repoUser.findByMail(mail);
            repoCat.save(new Category(name, propietario));
            return "Insertada categoría de nombre: " + name
                    + " y es propiedad de: " + propietario.toString();
        } catch (Exception e) {
            return "Excepción, no se ha encontrado el usuario con ese mail: " +
                    e.toString();
        }
    }
}
