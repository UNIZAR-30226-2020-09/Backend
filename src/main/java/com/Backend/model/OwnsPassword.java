package com.Backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Table
public class OwnsPassword {

    /* Clave compuesta */
    @EmbeddedId
    @Getter
    @Setter
    private OwnsPasswordKey key;

    /* Relacion 1:N con Password, extremo de la N */
    @ManyToOne
    @MapsId("passwordId")
    @Getter
    @Setter
    private Password password;

    /* Relacion 1:N con Password, extremo de la N */
    @ManyToOne(cascade=CascadeType.REMOVE)
    @MapsId("userId")
    @Getter
    @Setter
    private User user;

    /* Rol del usuario en la contraseña: 1 = Propietario, 0 = adjunto o seguidor */
    @Getter
    @Setter
    private int rol;

    public OwnsPassword(User user, Password password, int rol) {
        this.user = user;
        this.password = password;
        this.key = new OwnsPasswordKey(password.getId(), user.getId());
        this.rol = rol;
    }
}
