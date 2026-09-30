package co.edu.univalle.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Respuesta de un inicio de sesión exitoso (US-11). */
@Getter
@AllArgsConstructor
public class LoginResponseDTO {
    /** Token de sesión firmado, a enviar en el header Authorization de las siguientes peticiones. */
    private String token;
    /** Datos públicos del usuario autenticado. */
    private UsuarioResumenDTO usuario;
}
