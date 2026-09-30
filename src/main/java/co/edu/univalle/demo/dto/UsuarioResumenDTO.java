package co.edu.univalle.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Datos públicos de un usuario, sin información sensible (usado en la respuesta de login). */
@Getter
@AllArgsConstructor
public class UsuarioResumenDTO {
    /** Identificador del usuario. */
    private Long id;
    /** Nombre completo del usuario. */
    private String nombre;
    /** Correo electrónico del usuario. */
    private String email;
}
