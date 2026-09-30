package co.edu.univalle.demo.service;

import co.edu.univalle.demo.dto.HoyResponseDTO;
import co.edu.univalle.demo.exception.ConflictoSobrecargaException;
import co.edu.univalle.demo.exception.ResourceNotFoundException;
import co.edu.univalle.demo.exception.ValidacionException;
import co.edu.univalle.demo.model.EventoModel;
import co.edu.univalle.demo.model.HistorialReprogramacionModel;
import co.edu.univalle.demo.model.TareaLogisticaModel;
import co.edu.univalle.demo.repository.HistorialReprogramacionRepository;
import co.edu.univalle.demo.repository.TareaLogisticaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio que contiene la lógica de negocio para las tareas logísticas de un evento:
 * creación y edición del plan (US-02, US-03), reprogramación con detección de
 * conflicto por sobrecarga diaria (US-06, US-07, US-08) y registro de avance (US-09).
 */
@Service
public class TareaLogisticaService {

    /** Opciones sugeridas al usuario cuando se detecta un conflicto de sobrecarga (US-08). */
    private static final List<String> OPCIONES_RESOLUCION = List.of(
            "Mover la gestión a otro día",
            "Reducir las horas estimadas",
            "Posponer la gestión"
    );

    /** Repositorio de acceso a datos de tareas logísticas. */
    private final TareaLogisticaRepository tareaLogisticaRepository;

    /** Repositorio de historial de reprogramaciones. */
    private final HistorialReprogramacionRepository historialRepository;

    /** Servicio de eventos, usado para validar propiedad del evento dueño de la tarea. */
    private final EventoService eventoService;

    /** Servicio de usuarios, usado para obtener el límite diario configurado (US-12). */
    private final UsuarioService usuarioService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param tareaLogisticaRepository repositorio de tareas logísticas
     * @param historialRepository     repositorio de historial de reprogramaciones
     * @param eventoService           servicio de eventos
     * @param usuarioService          servicio de usuarios
     */
    public TareaLogisticaService(
            final TareaLogisticaRepository tareaLogisticaRepository,
            final HistorialReprogramacionRepository historialRepository,
            final EventoService eventoService,
            final UsuarioService usuarioService) {
        this.tareaLogisticaRepository = tareaLogisticaRepository;
        this.historialRepository = historialRepository;
        this.eventoService = eventoService;
        this.usuarioService = usuarioService;
    }

    /**
     * Crea una nueva tarea logística para un evento del usuario autenticado (US-02).
     *
     * @param eventoId  identificador del evento dueño
     * @param tarea     datos de la tarea a crear
     * @param usuarioId identificador del usuario autenticado
     * @return la tarea creada
     * @throws ValidacionException          si faltan datos obligatorios o son inválidos
     * @throws ConflictoSobrecargaException si la nueva tarea supera el límite diario (US-07)
     */
    @Transactional
    public TareaLogisticaModel crear(final Long eventoId, final TareaLogisticaModel tarea, final Long usuarioId) {
        final EventoModel evento = eventoService.obtenerPorId(eventoId, usuarioId);
        validarCamposMinimos(tarea);
        verificarSobrecarga(usuarioId, tarea.getFechaLimite(), tarea.getHorasEstimadas(), null);

        tarea.setId(null);
        tarea.setEventoId(evento.getId());
        tarea.setUsuarioId(usuarioId);
        tarea.setEstado(TareaLogisticaModel.ESTADO_PENDIENTE);
        tarea.setFechaEjecucion(null);
        return tareaLogisticaRepository.save(tarea);
    }

    /**
     * Retorna todas las tareas logísticas de un evento del usuario autenticado (US-02, US-03).
     *
     * @param eventoId  identificador del evento
     * @param usuarioId identificador del usuario autenticado
     * @return lista de tareas logísticas del evento
     */
    @Transactional(readOnly = true)
    public List<TareaLogisticaModel> listarPorEvento(final Long eventoId, final Long usuarioId) {
        eventoService.obtenerPorId(eventoId, usuarioId);
        return tareaLogisticaRepository.findByEventoId(eventoId);
    }

    /**
     * Actualiza el nombre, plazo y/u horas estimadas de una tarea logística (US-03).
     * Vuelve a evaluar la sobrecarga diaria con los nuevos valores (US-07).
     *
     * @param id        identificador de la tarea
     * @param cambios   nombre, plazo y horas estimadas nuevos
     * @param usuarioId identificador del usuario autenticado
     * @return la tarea actualizada
     * @throws ValidacionException          si los datos son inválidos
     * @throws ConflictoSobrecargaException si el cambio supera el límite diario (US-07)
     */
    @Transactional
    public TareaLogisticaModel actualizar(
            final Long id,
            final TareaLogisticaModel cambios,
            final Long usuarioId) {
        final TareaLogisticaModel existente = obtenerPropiaOFallar(id, usuarioId);
        validarCamposMinimos(cambios);
        verificarSobrecarga(usuarioId, cambios.getFechaLimite(), cambios.getHorasEstimadas(), id);

        existente.setTitulo(cambios.getTitulo());
        existente.setDescripcion(cambios.getDescripcion());
        existente.setFechaLimite(cambios.getFechaLimite());
        existente.setHorasEstimadas(cambios.getHorasEstimadas());
        return tareaLogisticaRepository.save(existente);
    }

    /**
     * Elimina una tarea logística del usuario autenticado (US-03).
     *
     * @param id        identificador de la tarea a eliminar
     * @param usuarioId identificador del usuario autenticado
     */
    @Transactional
    public void eliminar(final Long id, final Long usuarioId) {
        obtenerPropiaOFallar(id, usuarioId);
        tareaLogisticaRepository.deleteById(id);
    }

    /**
     * Marca una tarea logística como ejecutada o pospuesta, con nota opcional (US-09).
     *
     * @param id         identificador de la tarea
     * @param usuarioId  identificador del usuario autenticado
     * @param nuevoEstado "Completada" o "Pospuesta"
     * @param nota       nota opcional explicativa
     * @return la tarea actualizada
     * @throws ValidacionException si el estado solicitado no es válido
     */
    @Transactional
    public TareaLogisticaModel marcarEstado(
            final Long id,
            final Long usuarioId,
            final String nuevoEstado,
            final String nota) {
        final TareaLogisticaModel tarea = obtenerPropiaOFallar(id, usuarioId);
        if (!TareaLogisticaModel.ESTADO_COMPLETADA.equals(nuevoEstado)
                && !TareaLogisticaModel.ESTADO_POSPUESTA.equals(nuevoEstado)) {
            throw new ValidacionException(
                "El estado debe ser \"" + TareaLogisticaModel.ESTADO_COMPLETADA
                        + "\" o \"" + TareaLogisticaModel.ESTADO_POSPUESTA + "\"");
        }
        tarea.setEstado(nuevoEstado);
        tarea.setFechaEjecucion(LocalDate.now());
        tarea.setNotaEjecucion(nota);
        return tareaLogisticaRepository.save(tarea);
    }

    /**
     * Reprograma la fecha de una tarea logística, detectando conflicto de sobrecarga
     * diaria en el día destino (US-06, US-07). Cada intento queda registrado en el
     * historial de reprogramaciones, haya generado conflicto o no (US-08).
     *
     * @param id         identificador de la tarea
     * @param usuarioId  identificador del usuario autenticado
     * @param nuevoPlazo nueva fecha objetivo propuesta
     * @return la tarea con la fecha ya actualizada
     * @throws ConflictoSobrecargaException si el día destino supera el límite diario (US-07)
     */
    @Transactional
    public TareaLogisticaModel reprogramar(final Long id, final Long usuarioId, final LocalDate nuevoPlazo) {
        final TareaLogisticaModel tarea = obtenerPropiaOFallar(id, usuarioId);
        if (nuevoPlazo == null) {
            throw new ValidacionException("La nueva fecha es obligatoria");
        }
        final LocalDate fechaAnterior = tarea.getFechaLimite();

        try {
            verificarSobrecarga(usuarioId, nuevoPlazo, tarea.getHorasEstimadas(), id);
        } catch (final ConflictoSobrecargaException conflicto) {
            registrarHistorial(id, fechaAnterior, nuevoPlazo, true, conflicto.getMessage());
            throw conflicto;
        }

        tarea.setFechaLimite(nuevoPlazo);
        final TareaLogisticaModel guardada = tareaLogisticaRepository.save(tarea);
        registrarHistorial(id, fechaAnterior, nuevoPlazo, false, null);
        return guardada;
    }

    /**
     * Construye la vista "Hoy": agrupa las gestiones logísticas del usuario en
     * Vencidas, Para hoy y Próximas, ordenadas según la regla obligatoria
     * (por fecha y, en caso de empate, por menor esfuerzo estimado primero),
     * con filtros opcionales por evento y/o estado (US-04, US-05).
     *
     * @param usuarioId      identificador del usuario autenticado
     * @param eventoIdFiltro identificador de evento para acotar la vista (opcional, nulo = todos)
     * @param estadoFiltro   estado de gestión a mostrar (opcional; por defecto solo "Pendiente",
     *                       ya que Vencidas/Hoy/Próximas solo tienen sentido para gestiones no resueltas)
     * @return la vista Hoy con la regla de orden documentada y las tres listas agrupadas
     */
    @Transactional(readOnly = true)
    public HoyResponseDTO obtenerVistaHoy(
            final Long usuarioId,
            final Long eventoIdFiltro,
            final String estadoFiltro) {
        final String estadoEfectivo = (estadoFiltro == null || estadoFiltro.isBlank())
                ? TareaLogisticaModel.ESTADO_PENDIENTE
                : estadoFiltro;

        final List<TareaLogisticaModel> filtradas = tareaLogisticaRepository.findByUsuarioId(usuarioId).stream()
                .filter(t -> estadoEfectivo.equalsIgnoreCase(t.getEstado()))
                .filter(t -> eventoIdFiltro == null || eventoIdFiltro.equals(t.getEventoId()))
                .toList();

        final LocalDate hoy = LocalDate.now();
        final Comparator<TareaLogisticaModel> porFechaYEsfuerzo = Comparator
                .comparing(TareaLogisticaModel::getFechaLimite)
                .thenComparing(TareaLogisticaModel::getHorasEstimadas);

        final List<TareaLogisticaModel> vencidas = filtradas.stream()
                .filter(t -> t.getFechaLimite().isBefore(hoy))
                .sorted(porFechaYEsfuerzo)
                .toList();
        final List<TareaLogisticaModel> paraHoy = filtradas.stream()
                .filter(t -> t.getFechaLimite().isEqual(hoy))
                .sorted(Comparator.comparing(TareaLogisticaModel::getHorasEstimadas))
                .toList();
        final List<TareaLogisticaModel> proximas = filtradas.stream()
                .filter(t -> t.getFechaLimite().isAfter(hoy))
                .sorted(porFechaYEsfuerzo)
                .toList();

        final String regla = "Se muestran primero las gestiones Vencidas (más antigua arriba), "
                + "luego las de Hoy, y luego las Próximas por fecha más cercana. "
                + "En caso de empate en la misma fecha, se prioriza la de menor esfuerzo estimado.";
        return new HoyResponseDTO(regla, vencidas, paraHoy, proximas);
    }

    /**
     * Calcula cuántas horas de gestión ya tiene planificadas el usuario en una fecha
     * dada y, si sumar las nuevas horas supera su límite diario, lanza el conflicto.
     * Solo se cuentan tareas en estado Pendiente: las completadas o pospuestas ya no
     * "ocupan" el día para efectos de sobrecarga.
     *
     * @param usuarioId       identificador del usuario
     * @param fecha           fecha objetivo a evaluar
     * @param horasNuevas     horas que se quieren agregar/mantener ese día
     * @param excluirTareaId  id de la propia tarea a excluir del cálculo (nulo si es una tarea nueva)
     * @throws ConflictoSobrecargaException si el total supera el límite diario del usuario
     */
    private void verificarSobrecarga(
            final Long usuarioId,
            final LocalDate fecha,
            final BigDecimal horasNuevas,
            final Long excluirTareaId) {
        final BigDecimal limite = usuarioService.obtenerLimiteDiario(usuarioId);
        final BigDecimal horasExistentes = tareaLogisticaRepository.findByUsuarioId(usuarioId).stream()
                .filter(t -> TareaLogisticaModel.ESTADO_PENDIENTE.equals(t.getEstado()))
                .filter(t -> fecha.equals(t.getFechaLimite()))
                .filter(t -> excluirTareaId == null || !excluirTareaId.equals(t.getId()))
                .map(TareaLogisticaModel::getHorasEstimadas)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal total = horasExistentes.add(horasNuevas);

        if (total.compareTo(limite) > 0) {
            final String mensaje = "Quedarías con " + total + "h de gestión planificadas (límite " + limite + "h)";
            throw new ConflictoSobrecargaException(mensaje, total, limite, OPCIONES_RESOLUCION);
        }
    }

    /**
     * Registra un intento de reprogramación en el historial (US-08).
     *
     * @param tareaId           identificador de la tarea reprogramada
     * @param fechaAnterior     fecha que tenía antes del intento
     * @param fechaNueva        fecha propuesta
     * @param conflictoDetectado si el intento generó conflicto de sobrecarga
     * @param detalleConflicto  mensaje del conflicto (nulo si no hubo)
     */
    private void registrarHistorial(
            final Long tareaId,
            final LocalDate fechaAnterior,
            final LocalDate fechaNueva,
            final boolean conflictoDetectado,
            final String detalleConflicto) {
        historialRepository.save(HistorialReprogramacionModel.builder()
                .tareaId(tareaId)
                .fechaAnterior(fechaAnterior)
                .fechaNueva(fechaNueva)
                .conflictoDetectado(conflictoDetectado)
                .detalleConflicto(detalleConflicto)
                .build());
    }

    /**
     * Obtiene una tarea logística verificando que pertenezca al usuario autenticado.
     *
     * @param id        identificador de la tarea
     * @param usuarioId identificador del usuario autenticado
     * @return la tarea encontrada
     * @throws ResourceNotFoundException si la tarea no existe o no le pertenece al usuario
     */
    private TareaLogisticaModel obtenerPropiaOFallar(final Long id, final Long usuarioId) {
        final TareaLogisticaModel tarea = tareaLogisticaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tarea logística con id " + id + " no encontrada"));
        if (!tarea.getUsuarioId().equals(usuarioId)) {
            throw new ResourceNotFoundException("Tarea logística con id " + id + " no encontrada");
        }
        return tarea;
    }

    /**
     * Valida los campos mínimos de una tarea logística (US-02, Escenario 2).
     *
     * @param tarea tarea a validar
     * @throws ValidacionException si el nombre está vacío, el plazo falta o las horas son 0 o negativas
     */
    private void validarCamposMinimos(final TareaLogisticaModel tarea) {
        if (tarea.getTitulo() == null || tarea.getTitulo().isBlank()) {
            throw new ValidacionException("El nombre de la gestión es obligatorio");
        }
        if (tarea.getFechaLimite() == null) {
            throw new ValidacionException("El plazo de la gestión es obligatorio");
        }
        if (tarea.getHorasEstimadas() == null || tarea.getHorasEstimadas().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacionException("Las horas estimadas deben ser mayores a 0");
        }
    }

}
