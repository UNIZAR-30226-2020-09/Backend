package com.Backend.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@Table(name = Password.TABLE_NAME)
public class Password {

    public static final String TABLE_NAME= "Password";

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Getter
    @Setter
    private String passwordName;

    @Getter
    @Setter
    @Column(nullable = false)
    private String password;

    @Getter
    @Setter
    private String optionalText;

    @Getter
    @Setter
    private String userName;

    /* Relación 1:N con la tabla entre User y Password, extremo del 1*/
    @Getter
    @OneToMany(mappedBy = "password")
    private Set<OwnsPassword> usersSet = new HashSet<>();

    /*
     * Decidir el tipo de dato de la fecha de expiración
     * Existe tipo Date en java.
     * Date expirationDate;
     */

    /* Relación 1:N con categoría, extremo de la N */
    public static final String COLUMN_CAT_NAME = "cat_id";
    @ManyToOne
    @JoinColumn(name = Password.COLUMN_CAT_NAME)
    @Getter
    @Setter
    private Category category;

    public Password(String password, String passwordName) {
        this.password = password;
        this.passwordName = passwordName;
    }
}