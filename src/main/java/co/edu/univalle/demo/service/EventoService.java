package co.edu.univalle.demo.service;

import co.edu.univalle.demo.exception.AccesoDenegadoException;
import co.edu.univalle.demo.exception.ResourceNotFoundException;
import co.edu.univalle.demo.exception.ValidacionException;
import co.edu.univalle.demo.model.EventoModel;
import co.edu.univalle.demo.model.TareaLogisticaModel;
import co.edu.univalle.demo.repository.EventoRepository;
import co.edu.univalle.demo.repository.TareaLogisticaRepository;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio que contiene la lógica de negocio para la gestión de eventos (US-01, US-03, US-10).
 * Todas las consultas quedan aisladas por usuario: un usuario nunca puede ver, editar
 * o eliminar eventos de otro (US-11, Escenario 4).
 */
@Service
public class EventoService {

    /** Repositorio de acceso a datos de eventos. */
    private final EventoRepository eventoRepository;

    /** Repositorio de tareas logísticas, usado para calcular el progreso (US-10). */
    private final TareaLogisticaRepository tareaLogisticaRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param eventoRepository         repositorio de eventos
     * @param tareaLogisticaRepository repositorio de tareas logísticas
     */
    public EventoService(
            final EventoRepository eventoRepository,
            final TareaLogisticaRepository tareaLogisticaRepository) {
        this.eventoRepository = eventoRepository;
        this.tareaLogisticaRepository = tareaLogisticaRepository;
    }

    /**
     * Crea un nuevo evento en el sistema, asociado siempre al usuario autenticado.
     *
     * @param evento    datos del evento a crear
     * @param usuarioId identificador del usuario autenticado (nunca del cuerpo de la petición)
     * @return el evento creado con su id asignado
     * @throws ValidacionException si faltan los campos mínimos requeridos (US-01)
     */
    @Transactional
    public EventoModel crear(final EventoModel evento, final Long usuarioId) {
        validarCamposMinimos(evento);
        evento.setUsuarioId(usuarioId);
        if (evento.getEstado() == null || evento.getEstado().isBlank()) {
            evento.setEstado("Planificación");
        }
        return eventoRepository.save(evento);
    }

    /**
     * Actualiza los datos de un evento existente, siempre que pertenezca al usuario autenticado.
     *
     * @param evento    datos actualizados del evento con id válido
     * @param usuarioId identificador del usuario autenticado
     * @return el evento actualizado
     * @throws ResourceNotFoundException si el evento no existe
     * @throws AccesoDenegadoException   si el evento no pertenece al usuario autenticado
     */
    @Transactional
    public EventoModel actualizar(final EventoModel evento, final Long usuarioId) {
        final EventoModel existente = obtenerPropioOFallar(evento.getId(), usuarioId);
        validarCamposMinimos(evento);
        evento.setUsuarioId(existente.getUsuarioId());
        return eventoRepository.save(evento);
    }

    /**
     * Elimina un evento por su id, siempre que pertenezca al usuario autenticado.
     *
     * @param id        identificador del evento a eliminar
     * @param usuarioId identificador del usuario autenticado
     * @throws ResourceNotFoundException si el evento no existe
     * @throws AccesoDenegadoException   si el evento no pertenece al usuario autenticado
     */
    @Transactional
    public void eliminar(final Long id, final Long usuarioId) {
        obtenerPropioOFallar(id, usuarioId);
        eventoRepository.deleteById(id);
    }

    /**
     * Retorna todos los eventos del usuario autenticado.
     *
     * @param usuarioId identificador del usuario autenticado
     * @return lista de eventos del usuario
     */
    @Transactional(readOnly = true)
    public List<EventoModel> obtenerTodos(final Long usuarioId) {
        return eventoRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Busca un evento por su id, siempre que pertenezca al usuario autenticado.
     *
     * @param id        identificador del evento
     * @param usuarioId identificador del usuario autenticado
     * @return el evento encontrado
     * @throws ResourceNotFoundException si el evento no existe o no le pertenece al usuario
     */
    @Transactional(readOnly = true)
    public EventoModel obtenerPorId(final Long id, final Long usuarioId) {
        return obtenerPropioOFallar(id, usuarioId);
    }

    /**
     * Busca, entre los eventos del usuario autenticado, los que coincidan parcialmente
     * con un nombre.
     *
     * @param nombre    fragmento del nombre a buscar
     * @param usuarioId identificador del usuario autenticado
     * @return lista de eventos que coinciden
     */
    @Transactional(readOnly = true)
    public List<EventoModel> buscarPorNombre(final String nombre, final Long usuarioId) {
        return eventoRepository.findByUsuarioId(usuarioId).stream()
                .filter(e -> e.getNombre() != null && e.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }

    /**
     * Busca, entre los eventos del usuario autenticado, los que tengan un estado específico.
     *
     * @param estado    estado del evento (ej. Planificación, En Progreso)
     * @param usuarioId identificador del usuario autenticado
     * @return lista de eventos que coinciden con el estado
     */
    @Transactional(readOnly = true)
    public List<EventoModel> buscarPorEstado(final String estado, final Long usuarioId) {
        return eventoRepository.findByUsuarioId(usuarioId).stream()
                .filter(e -> estado.equalsIgnoreCase(e.getEstado()))
                .toList();
    }

    /**
     * Calcula el progreso de preparación de un evento a partir del estado de sus
     * tareas logísticas (US-10).
     *
     * @param id        identificador del evento
     * @param usuarioId identificador del usuario autenticado
     * @return mapa con el total de tareas, las completadas y el porcentaje de avance
     * @throws ResourceNotFoundException si el evento no existe o no le pertenece al usuario
     */
    @Transactional(readOnly = true)
    public Map<String, Object> calcularProgreso(final Long id, final Long usuarioId) {
        obtenerPropioOFallar(id, usuarioId);
        final List<TareaLogisticaModel> tareas = tareaLogisticaRepository.findByEventoId(id);
        final long total = tareas.size();
        final long completadas = tareas.stream()
                .filter(t -> TareaLogisticaModel.ESTADO_COMPLETADA.equals(t.getEstado()))
                .count();
        final int porcentaje = total == 0 ? 0 : (int) Math.round((completadas * 100.0) / total);
        return Map.of(
                "total", total,
                "completadas", completadas,
                "porcentaje", porcentaje
        );
    }

    /**
     * Obtiene un evento por id verificando que pertenezca al usuario autenticado.
     *
     * @param id        identificador del evento
     * @param usuarioId identificador del usuario autenticado
     * @return el evento encontrado
     * @throws ResourceNotFoundException si el evento no existe o no le pertenece al usuario
     *                                   (se usa 404 en ambos casos para no revelar si el evento existe)
     */
    private EventoModel obtenerPropioOFallar(final Long id, final Long usuarioId) {
        final EventoModel evento = eventoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Evento con id " + id + " no encontrado"
            ));
        if (!evento.getUsuarioId().equals(usuarioId)) {
            throw new ResourceNotFoundException("Evento con id " + id + " no encontrado");
        }
        return evento;
    }

    /**
     * Valida los campos mínimos requeridos para crear o editar un evento (US-01, Escenario 2).
     *
     * @param evento evento a validar
     * @throws ValidacionException si falta algún campo obligatorio
     */
    private void validarCamposMinimos(final EventoModel evento) {
        if (evento.getNombre() == null || evento.getNombre().isBlank()) {
            throw new ValidacionException("El nombre del evento es obligatorio");
        }
        if (evento.getFechaEvento() == null) {
            throw new ValidacionException("La fecha del evento es obligatoria");
        }
    }

}
