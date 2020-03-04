package com.Backend.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = Category.TABLE_NAME)
public class Category {

    public static final String TABLE_NAME= "Category";

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false)
    private String categoryName;

    /* Relación 1:N con categoría, extremo del 1 */
    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private Set<Password> passwordSet;

    // fetch = FetchType.LAZY o fetch = FetchType.EAGER, aún no lo he estudiado bien
    /* Relación 1:N con usuario, extremo de la N */
    public static final String COLUMN_CAT_NAME = "user_id";
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Category.COLUMN_CAT_NAME)
    private User usuario;

    protected Category() {}

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "User{" +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}