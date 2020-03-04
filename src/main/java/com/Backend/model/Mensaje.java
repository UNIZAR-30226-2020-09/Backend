package com.Backend.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = Mensaje.TABLE_NAME)
public class Mensaje {

    public static final String TABLE_NAME= "Mensaje";

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false)
    private String mailMessage;

    @Getter
    @Setter
    @Column(nullable = false)
    private String bodyMessage;

    public Mensaje(String mailMessage, String bodyMessage) {
        this.bodyMessage = bodyMessage;
        this.mailMessage = mailMessage;
    }

    public Mensaje(){}
}
