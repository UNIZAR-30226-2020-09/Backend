package com.Backend.model;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class OwnsPasswordKey implements Serializable {

    /*
     * Clase que representa la clave compuesta de la entidad
     * situada entre User y Password (Posee)
     */
    private Long passwordId;

    private Long userId;

    public OwnsPasswordKey(Long passwordId, Long userId) {
        this.passwordId = passwordId;
        this.userId = userId;
    }

    public OwnsPasswordKey() {}
}
