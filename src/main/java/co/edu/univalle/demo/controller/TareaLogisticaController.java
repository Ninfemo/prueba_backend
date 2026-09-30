package co.edu.univalle.demo.controller;

import co.edu.univalle.demo.dto.MarcarEstadoRequestDTO;
import co.edu.univalle.demo.dto.ReprogramarRequestDTO;
import co.edu.univalle.demo.model.TareaLogisticaModel;
import co.edu.univalle.demo.security.AuthContext;
import co.edu.univalle.demo.service.TareaLogisticaService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para las tareas (subtareas) logísticas de un evento
 * (US-02, US-03, US-06, US-07, US-08, US-09). Todos los endpoints operan
 * exclusivamente sobre las tareas del usuario autenticado.
 */
@RestController
public class TareaLogisticaController {

    /** Servicio de lógica de negocio para tareas logísticas. */
    private final TareaLogisticaService tareaLogisticaService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param tareaLogisticaService servicio de tareas logísticas
     */
    public TareaLogisticaController(final TareaLogisticaService tareaLogisticaService) {
        this.tareaLogisticaService = tareaLogisticaService;
    }

    /**
     * Crea una nueva tarea logística para un evento del usuario autenticado (US-02).
     *
     * @param eventoId identificador del evento dueño
     * @param tarea    datos de la tarea a crear
     * @param request  petición HTTP (para obtener el usuario autenticado)
     * @return la tarea creada con código HTTP 201
     */
    @PostMapping("/api/eventos/{eventoId}/tareas")
    @ResponseStatus(HttpStatus.CREATED)
    public TareaLogisticaModel crear(
            @PathVariable final Long eventoId,
            @RequestBody final TareaLogisticaModel tarea,
            final HttpServletRequest request) {
        return tareaLogisticaService.crear(eventoId, tarea, AuthContext.usuarioActual(request));
    }

    /**
     * Lista las tareas logísticas de un evento del usuario autenticado (US-02, US-03).
     *
     * @param eventoId identificador del evento
     * @param request  petición HTTP (para obtener el usuario autenticado)
     * @return lista de tareas logísticas del evento
     */
    @GetMapping("/api/eventos/{eventoId}/tareas")
    public List<TareaLogisticaModel> listarPorEvento(
            @PathVariable final Long eventoId,
            final HttpServletRequest request) {
        return tareaLogisticaService.listarPorEvento(eventoId, AuthContext.usuarioActual(request));
    }

    /**
     * Actualiza el nombre, plazo y/u horas estimadas de una tarea logística (US-03).
     *
     * @param id      identificador de la tarea
     * @param tarea   datos actualizados
     * @param request petición HTTP (para obtener el usuario autenticado)
     * @return la tarea actualizada
     */
    @PutMapping("/api/tareas/{id}")
    public TareaLogisticaModel actualizar(
            @PathVariable final Long id,
            @RequestBody final TareaLogisticaModel tarea,
            final HttpServletRequest request) {
        return tareaLogisticaService.actualizar(id, tarea, AuthContext.usuarioActual(request));
    }

    /**
     * Elimina una tarea logística del usuario autenticado (US-03).
     *
     * @param id      identificador de la tarea a eliminar
     * @param request petición HTTP (para obtener el usuario autenticado)
     */
    @DeleteMapping("/api/tareas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable final Long id, final HttpServletRequest request) {
        tareaLogisticaService.eliminar(id, AuthContext.usuarioActual(request));
    }

    /**
     * Marca una tarea logística como ejecutada o pospuesta, con nota opcional (US-09).
     *
     * @param id      identificador de la tarea
     * @param cuerpo  nuevo estado y nota opcional
     * @param request petición HTTP (para obtener el usuario autenticado)
     * @return la tarea actualizada
     */
    @PatchMapping("/api/tareas/{id}/estado")
    public TareaLogisticaModel marcarEstado(
            @PathVariable final Long id,
            @RequestBody final MarcarEstadoRequestDTO cuerpo,
            final HttpServletRequest request) {
        return tareaLogisticaService.marcarEstado(
                id, AuthContext.usuarioActual(request), cuerpo.getEstado(), cuerpo.getNota());
    }

    /**
     * Reprograma la fecha de una tarea logística, detectando conflicto de sobrecarga
     * diaria en el día destino (US-06, US-07, US-08).
     *
     * @param id      identificador de la tarea
     * @param cuerpo  nueva fecha objetivo propuesta
     * @param request petición HTTP (para obtener el usuario autenticado)
     * @return la tarea con la fecha ya actualizada (o HTTP 409 si hay conflicto de sobrecarga)
     */
    @PutMapping("/api/tareas/{id}/reprogramar")
    public TareaLogisticaModel reprogramar(
            @PathVariable final Long id,
            @RequestBody final ReprogramarRequestDTO cuerpo,
            final HttpServletRequest request) {
        return tareaLogisticaService.reprogramar(
                id, AuthContext.usuarioActual(request), cuerpo.getNuevoPlazo());
    }

}
