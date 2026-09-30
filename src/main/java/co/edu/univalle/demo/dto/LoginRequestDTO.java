package co.edu.univalle.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Cuerpo de la petición de inicio de sesión (US-11). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    /** Correo electrónico del usuario. */
    private String email;
    /** Contraseña en texto plano. */
    private String password;
}
