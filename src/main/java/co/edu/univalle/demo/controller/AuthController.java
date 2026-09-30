package co.edu.univalle.demo.controller;

import co.edu.univalle.demo.dto.LoginRequestDTO;
import co.edu.univalle.demo.dto.LoginResponseDTO;
import co.edu.univalle.demo.dto.UsuarioResumenDTO;
import co.edu.univalle.demo.exception.ValidacionException;
import co.edu.univalle.demo.model.UsuarioModel;
import co.edu.univalle.demo.security.TokenService;
import co.edu.univalle.demo.service.UsuarioService;
import java.util.Optional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST de autenticación mínima (US-11). Esta es la única ruta de
 * negocio pública de toda la API: sin ella, {@code AuthFilter} nunca podría
 * emitir el primer token y ningún otro endpoint sería alcanzable.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /** Mensaje genérico de credenciales inválidas (nunca revela si el correo existe). */
    private static final String MENSAJE_CREDENCIALES_INVALIDAS = "Credenciales inválidas";

    /** Servicio de lógica de negocio para usuarios. */
    private final UsuarioService usuarioService;

    /** Servicio de generación de tokens firmados. */
    private final TokenService tokenService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param usuarioService servicio de usuarios
     * @param tokenService   servicio de tokens
     */
    public AuthController(final UsuarioService usuarioService, final TokenService tokenService) {
        this.usuarioService = usuarioService;
        this.tokenService = tokenService;
    }

    /**
     * Inicia sesión con correo y contraseña (US-11, Escenarios 1 y 2).
     *
     * @param credenciales correo y contraseña en texto plano
     * @return token firmado y datos públicos del usuario autenticado
     * @throws ValidacionException si las credenciales no son válidas (sin revelar
     *                             si el problema fue el correo o la contraseña)
     */
    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody final LoginRequestDTO credenciales) {
        final Optional<UsuarioModel> usuario = usuarioService.verificarCredenciales(
                credenciales.getEmail(), credenciales.getPassword());
        if (usuario.isEmpty()) {
            throw new ValidacionException(MENSAJE_CREDENCIALES_INVALIDAS);
        }
        final UsuarioModel encontrado = usuario.get();
        final String token = tokenService.generarToken(encontrado.getId());
        final UsuarioResumenDTO resumen = new UsuarioResumenDTO(
                encontrado.getId(), encontrado.getNombre(), encontrado.getEmail());
        return new LoginResponseDTO(token, resumen);
    }

}
