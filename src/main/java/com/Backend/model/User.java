package com.Backend.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = User.TABLE_NAME)
public class User {

    public static final String TABLE_NAME= "Pandora_User";

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Getter
    @Setter
    @Column(unique = true, nullable = false)
    private String mail;

    @Getter
    @Setter
    @Column(nullable = false)
    private String masterPassword;

    /* Relacion M:N con password, este método tiene un problema, no existe
    *  el concepto de añadir atributos a las relaciones N:M, por lo que no
    *  podemos hacerlo así, se sugiere utilizar una clase intermedia con una
    *  relación 1:N a cada una de las otras dos tablas, como dice aquí:
    *  https://stackoverflow.com/questions/9816932/mapping-extra-attribute-in-a-join-table-jpa-2
    */
    /*@JoinTable(

            name = "Posee",
            joinColumns = @JoinColumn(name = "id_User", nullable = false),
            inverseJoinColumns = @JoinColumn(name="id_Pass", nullable = false)
    )
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Category> authors;
    */

    // Se puede añadir que las operaciones sean en cascada si interesa
    /* Relación 1:N con categoría, extremo del 1 */
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Set<Category> categorySet;

    protected User() {}

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

