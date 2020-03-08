package com.Backend.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = User.TABLE_NAME)
public class User {

    public static final String TABLE_NAME= "Pandora_User";

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    // Considerar utilizar @NaturalId, probablemente sea mejor opción,
    // debo investigar todavía esta opción
    private String mail;

    @Column(nullable = false)
    private String masterPassword;

    /*
     * Relacion M:N con password, este método tiene un problema, no existe
     *  el concepto de añadir atributos a las relaciones N:M, por lo que no
     *  podemos hacerlo así, se sugiere utilizar una clase intermedia con una
     *  relación 1:N a cada una de las otras dos tablas, como dice aquí:
     *  https://vladmihalcea.com/the-best-way-to-map-a-many-to-many-association-with-extra-columns-when-using-jpa-and-hibernate/
     *  Otra opción era utilizar el idClass como dice aquí, pero la comparación entre ambas opciones
     *  no ofrece, de momento, una solución definitiva que sea más conveniente.
     *  https://www.baeldung.com/jpa-composite-primary-keys
     */
    /*
     * @OneToMany( mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true) ¿En cascada?
     * Relación 1:N con la tabla entre User y Password, extremo del 1
     */
    @OneToMany(mappedBy = "user")
    private List<OwnsPassword> passwords;


    /* Relación 1:N con categoría, extremo del 1 */
    @OneToMany(mappedBy = "usuario")
    private Set<Category> categorySet;

    public User() {}

    public User(String mail, String masterPassword) {
        this.masterPassword = masterPassword;
        this.mail = mail;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", mail='" + mail + '\'' +
                ", masterPassword='" + masterPassword + '\'' +
                '}';
    }

}

