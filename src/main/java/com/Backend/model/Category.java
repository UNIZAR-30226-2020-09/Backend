package com.Backend.model;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@NoArgsConstructor
/*@Table(
        uniqueConstraints=
        @UniqueConstraint(columnNames={"categoryName", "user_id"})
)*/
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

    /*
     * Se puede decidir entre cargar todos los atributos de la entidad inmediatamente
     * o que un proxy los cargue cuando interese
     * fetch = FetchType.LAZY o fetch = FetchType.EAGER, está la opción por defecto actualmente
     */

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

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}