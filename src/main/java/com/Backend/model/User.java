package com.Backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Table(name = User.TABLE_NAME)
public class User {

    public static final String TABLE_NAME= "Pandora_User";

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;

    @Column(unique = true, nullable = false)
    @Getter
    @Setter
    private String mail; // Se corresponde cin el mail

    @Setter
    @Getter
    private String token;

    @Column(nullable = false)
    @Getter
    @Setter
    private String masterPassword;

    /*
     * @OneToMany( mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true) ¿En cascada?
     * Relación 1:N con la tabla entre User y Password, extremo del 1
     */
    @OneToMany(mappedBy = "user")
    @Getter
    private Set<OwnsPassword> passwordSet = new HashSet<>();

    /* Relación 1:N con categoría, extremo del 1 */
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @Getter
    private Set<Category> categorySet = new HashSet<>();
    
    public User(String mail, String masterPassword) {
        this.masterPassword = masterPassword;
        this.mail = mail;
    }
}

