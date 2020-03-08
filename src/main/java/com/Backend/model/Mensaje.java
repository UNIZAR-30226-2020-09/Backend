package com.Backend.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Data
@Entity
@Table
public class Mensaje {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String mailMessage;

    @Column(nullable = false)
    private String bodyMessage;

    public Mensaje(String mailMessage, String bodyMessage) {
        this.bodyMessage = bodyMessage;
        this.mailMessage = mailMessage;
    }

    public Mensaje(){}
}
