package com.Backend.model;

import javax.persistence.*;

@Entity
@Table
public class OwnsPassword {

    /*
     * Decisión tomada, clase compuesta con IdClass o con Embedded class.
     */
    /* Clave compuesta */
    @EmbeddedId
    private OwnsPasswordKey key;

    /* Relacion 1:N con Password, extremo de la N */
    @ManyToOne
    @MapsId("passwordId")
    private Password password;

    /* Relacion 1:N con Password, extremo de la N */
    @ManyToOne
    @MapsId("userId")
    private User user;

    /* Rol del usuario en la contraseña: 1 = Propietario, 0 = adjunto o seguidor */
    private int rol;

    public OwnsPassword(User user, Password password, int rol) {
        this.user = user;
        this.password = password;
        this.key = new OwnsPasswordKey(user.getId(), password.getId());
        this.rol = rol;
    }

    public OwnsPassword() {}
}
