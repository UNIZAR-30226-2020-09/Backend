package com.Backend.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
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

    /*
    Por decidir el tipo de dato de la fecha de expiración
    @Getter
    @Setter
    @Column(name = "expirationDate", nullable = false)
    private int expirationDate;
    */

    /* Relación 1:N con categoría, extremo de la N */
    public static final String COLUMN_CAT_NAME = "cat_id";
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Password.COLUMN_CAT_NAME)
    private Category categoria;

    protected Password() {}

    public Password(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                '}';
    }
}