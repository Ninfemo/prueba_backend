package co.edu.univalle.demo.controller;

import co.edu.univalle.demo.model.EventoModel;
import co.edu.univalle.demo.security.AuthContext;
import co.edu.univalle.demo.service.EventoService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la gestión de eventos (US-01, US-03, US-10).
 * Todos los endpoints operan exclusivamente sobre los eventos del usuario autenticado.
 */
@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    /** Servicio de lógica de negocio para eventos. */
    private final EventoService eventoService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param eventoService servicio de eventos
     */
    public EventoController(final EventoService eventoService) {
        this.eventoService = eventoService;
    }

    /**
     * Crea un nuevo evento para el usuario autenticado.
     *
     * @param evento  datos del evento a crear
     * @param request petición HTTP (para obtener el usuario autenticado)
     * @return el evento creado con código HTTP 201
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventoModel crear(@RequestBody final EventoModel evento, final HttpServletRequest request) {
        return eventoService.crear(evento, AuthContext.usuarioActual(request));
    }

    /**
     * Actualiza los datos de un evento existente del usuario autenticado.
     *
     * @param id      identificador del evento
     * @param evento  datos actualizados
     * @param request petición HTTP (para obtener el usuario autenticado)
     * @return el evento actualizado
     */
    @PutMapping("/{id}")
    public EventoModel actualizar(
            @PathVariable final Long id,
            @RequestBody final EventoModel evento,
            final HttpServletRequest request) {
        evento.setId(id);
        return eventoService.actualizar(evento, AuthContext.usuarioActual(request));
    }

    /**
     * Elimina un evento del usuario autenticado.
     *
     * @param id      identificador del evento a eliminar
     * @param request petición HTTP (para obtener el usuario autenticado)
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable final Long id, final HttpServletRequest request) {
        eventoService.eliminar(id, AuthContext.usuarioActual(request));
    }

    /**
     * Obtiene la lista de todos los eventos del usuario autenticado.
     *
     * @param request petición HTTP (para obtener el usuario autenticado)
     * @return lista de eventos propios
     */
    @GetMapping
    public List<EventoModel> obtenerTodos(final HttpServletRequest request) {
        return eventoService.obtenerTodos(AuthContext.usuarioActual(request));
    }

    /**
     * Busca un evento del usuario autenticado por su identificador único.
     *
     * @param id      identificador del evento
     * @param request petición HTTP (para obtener el usuario autenticado)
     * @return el evento encontrado
     */
    @GetMapping("/{id}")
    public EventoModel obtenerPorId(@PathVariable final Long id, final HttpServletRequest request) {
        return eventoService.obtenerPorId(id, AuthContext.usuarioActual(request));
    }

    /**
     * Busca, entre los eventos del usuario autenticado, los que coincidan parcialmente con un nombre.
     *
     * @param nombre  fragmento del nombre a buscar
     * @param request petición HTTP (para obtener el usuario autenticado)
     * @return lista de eventos coincidentes
     */
    @GetMapping("/buscar")
    public List<EventoModel> buscarPorNombre(@RequestParam final String nombre, final HttpServletRequest request) {
        return eventoService.buscarPorNombre(nombre, AuthContext.usuarioActual(request));
    }

    /**
     * Busca, entre los eventos del usuario autenticado, los que tengan un estado específico.
     *
     * @param estado  estado del evento (ej. Planificación, En Progreso)
     * @param request petición HTTP (para obtener el usuario autenticado)
     * @return lista de eventos que coinciden con el estado
     */
    @GetMapping("/estado")
    public List<EventoModel> buscarPorEstado(@RequestParam final String estado, final HttpServletRequest request) {
        return eventoService.buscarPorEstado(estado, AuthContext.usuarioActual(request));
    }

    /**
     * Obtiene el progreso de preparación de un evento del usuario autenticado (US-10).
     *
     * @param id      identificador del evento
     * @param request petición HTTP (para obtener el usuario autenticado)
     * @return mapa con total de tareas, completadas y porcentaje de avance
     */
    @GetMapping("/{id}/progreso")
    public Map<String, Object> obtenerProgreso(@PathVariable final Long id, final HttpServletRequest request) {
        return eventoService.calcularProgreso(id, AuthContext.usuarioActual(request));
    }

}
