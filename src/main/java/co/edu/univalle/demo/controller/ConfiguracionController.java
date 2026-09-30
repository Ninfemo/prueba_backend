package co.edu.univalle.demo.controller;

import co.edu.univalle.demo.dto.LimiteDiarioDTO;
import co.edu.univalle.demo.security.AuthContext;
import co.edu.univalle.demo.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para configurar el límite diario de horas de gestión
 * del usuario autenticado (US-12).
 */
@RestController
@RequestMapping("/api/configuracion/limite-diario")
public class ConfiguracionController {

    /** Servicio de lógica de negocio para usuarios. */
    private final UsuarioService usuarioService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param usuarioService servicio de usuarios
     */
    public ConfiguracionController(final UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Obtiene el límite diario configurado por el usuario autenticado, o el
     * valor por defecto (6h) si nunca lo ha configurado (US-12, Escenario 1).
     *
     * @param request petición HTTP (para obtener el usuario autenticado)
     * @return el límite diario vigente
     */
    @GetMapping
    public LimiteDiarioDTO obtener(final HttpServletRequest request) {
        final Long usuarioId = AuthContext.usuarioActual(request);
        return new LimiteDiarioDTO(usuarioService.obtenerLimiteDiario(usuarioId));
    }

    /**
     * Actualiza el límite diario del usuario autenticado, validando el rango
     * permitido de 1 a 16 horas (US-12, Escenarios 2 y 3).
     *
     * @param cuerpo  nuevo límite diario propuesto
     * @param request petición HTTP (para obtener el usuario autenticado)
     * @return el límite diario ya guardado
     */
    @PutMapping
    public LimiteDiarioDTO actualizar(
            @RequestBody final LimiteDiarioDTO cuerpo,
            final HttpServletRequest request) {
        final Long usuarioId = AuthContext.usuarioActual(request);
        return new LimiteDiarioDTO(
                usuarioService.actualizarLimiteDiario(usuarioId, cuerpo.getLimiteHorasDiarias()));
    }

}
