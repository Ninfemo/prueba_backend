package co.edu.univalle.demo.controller;

import co.edu.univalle.demo.dto.HoyResponseDTO;
import co.edu.univalle.demo.security.AuthContext;
import co.edu.univalle.demo.service.TareaLogisticaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST de la vista "Hoy": destaca las gestiones urgentes del
 * usuario autenticado agrupadas en Vencidas / Para hoy / Próximas, con
 * filtros opcionales por evento y/o estado (US-04, US-05).
 */
@RestController
@RequestMapping("/api/hoy")
public class HoyController {

    /** Servicio de lógica de negocio para tareas logísticas. */
    private final TareaLogisticaService tareaLogisticaService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param tareaLogisticaService servicio de tareas logísticas
     */
    public HoyController(final TareaLogisticaService tareaLogisticaService) {
        this.tareaLogisticaService = tareaLogisticaService;
    }

    /**
     * Obtiene la vista "Hoy" del usuario autenticado, opcionalmente filtrada
     * por evento y/o estado de gestión (US-04, US-05).
     *
     * @param eventoId identificador de evento para acotar la vista (opcional)
     * @param estado   estado de gestión a mostrar (opcional; por defecto "Pendiente")
     * @param request  petición HTTP (para obtener el usuario autenticado)
     * @return la vista Hoy agrupada en Vencidas, Para hoy y Próximas
     */
    @GetMapping
    public HoyResponseDTO obtener(
            @RequestParam(required = false) final Long eventoId,
            @RequestParam(required = false) final String estado,
            final HttpServletRequest request) {
        return tareaLogisticaService.obtenerVistaHoy(AuthContext.usuarioActual(request), eventoId, estado);
    }

}
