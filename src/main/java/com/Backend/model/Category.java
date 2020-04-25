package com.Backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
public class Category {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false)
    private String categoryName;

    /* Relación 1:N con password, extremo del 1 */
    @Getter
    @OneToMany(mappedBy = "category")
    private Set<Password> passwordSet = new HashSet<>();

    /* Relación 1:N con usuario, extremo de la N */
    public static final String COLUMN_CAT_NAME = "FK_User";
    @ManyToOne
    @JoinColumn(name = Category.COLUMN_CAT_NAME, nullable = false)
    @Getter
    private User usuario;

    public Category(String categoryName, User usuario) {
        this.usuario = usuario;
        this.categoryName = categoryName;
    }
}