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
    @ManyToOne
    @MapsId("userId")
    @Getter
    @Setter
    private User user;

    /* Rol del usuario en la contraseña: 1 = Propietario, 0 = adjunto o seguidor */
    @Getter
    @Setter
    private int rol;

    /* Marca si la contraseña es compartida o no*/
    @Getter
    @Setter
    private int grupo;

    public OwnsPassword(User user, Password password, int rol, int grupo) {
        this.user = user;
        this.password = password;
        this.key = new OwnsPasswordKey(user.getId(), password.getId());
        this.rol = rol;
        this.grupo = grupo;
    }
}
