package com.Backend.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = Password.TABLE_NAME)
public class Password {

    public static final String TABLE_NAME= "Password";

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String passwordName;

    @Column(nullable = false)
    private String password;

    private String optionalText;

    private String userName;

    /* Relación 1:N con la tabla entre User y Password, extremo del 1*/
    @OneToMany(mappedBy = "password")
    private List<OwnsPassword> users;

    /*
     * Decidir el tipo de dato de la fecha de expiración
     * Existe tipo Date en java.
     */

    /* Relación 1:N con categoría, extremo de la N */
    public static final String COLUMN_CAT_NAME = "cat_id";
    @ManyToOne
    @JoinColumn(name = Password.COLUMN_CAT_NAME)
    private Category category;

    public Password() {}

    public Password(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                '}';
    }
}