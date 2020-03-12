package com.Backend.model;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class OwnsPasswordKey implements Serializable {

    /*
     * Clase que representa la clave compuesta de la entidad
     * situada entre User y Password (Posee)
     */
    @Getter
    @Setter
    private Long passwordId;

    @Getter
    @Setter
    private Long userId;

    public OwnsPasswordKey(Long passwordId, Long userId) {
        this.passwordId = passwordId;
        this.userId = userId;
    }
}
