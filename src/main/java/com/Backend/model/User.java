package com.Backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.jboss.aerogear.security.otp.api.Base32;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Table(name = User.TABLE_NAME)
public class User {

    public static final String TABLE_NAME= "Pandora_User";

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Getter
    @Setter
    private String id;

    @Column(unique = true, nullable = false)
    @Getter
    @Setter
    private String mail; // Se corresponde con el mail

    @Column(nullable = false)
    @Getter
    @Setter
    private String masterPassword;

    @Getter
    @Setter
    private Boolean mailVerified;

    @Getter
    @Setter
    private String secret;

    @Getter
    @Setter
    private Long secretExpirationTime;

    @Getter
    @Setter
    private Boolean loggedIn2FA;

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
        this.secret = Base32.random().substring(0, 5);
        this.secretExpirationTime = System.currentTimeMillis() + 60000; // 1 minuto
        this.loggedIn2FA = false;
        this.mailVerified = false;
    }

    public void updateSecret(){
        this.secret = Base32.random().substring(0, 5);
        this.secretExpirationTime = System.currentTimeMillis() + 60000; // 1 minuto
    }
}

