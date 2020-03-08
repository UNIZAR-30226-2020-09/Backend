package com.Backend.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table
public class Category {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String categoryName;

    /* Relación 1:N con categoría, extremo del 1 */
    @OneToMany(mappedBy = "category")
    private Set<Password> passwordSet;

    /*
     * Se puede decidir entre cargar todos los atributos de la entidad inmediatamente
     * o que un proxy los cargue cuando interese
     * fetch = FetchType.LAZY o fetch = FetchType.EAGER, está la opción por defecto actualmente
     */

    /* Relación 1:N con usuario, extremo de la N */
    public static final String COLUMN_CAT_NAME = "user_id";
    @ManyToOne
    @JoinColumn(name = Category.COLUMN_CAT_NAME, nullable = false)
    private User usuario;

    public Category(String categoryName, User usuario) {
        this.usuario = usuario;
        this.categoryName = categoryName;
    }

    public Category(){}

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", categoryName='" + categoryName + '\'' +
                ", usuario=" + usuario +
                '}';
    }
}