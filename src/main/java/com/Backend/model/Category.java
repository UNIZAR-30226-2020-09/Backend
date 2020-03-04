package com.Backend.model;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Getter
    @Setter
    private String categoryName;

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

    public void displayCategoryInfo(){
        System.out.println("Category name is: " + categoryName);
    }


}