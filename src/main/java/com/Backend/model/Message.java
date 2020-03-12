package com.Backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table
public class Message {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String mailMessage;

    @Column(nullable = false)
    private String bodyMessage;

    public Message(String mailMessage, String bodyMessage) {
        this.bodyMessage = bodyMessage;
        this.mailMessage = mailMessage;
    }
}
