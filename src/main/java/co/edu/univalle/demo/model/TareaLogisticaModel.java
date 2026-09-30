package co.edu.univalle.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA que representa una tarea (subtarea) logística de un evento
 * (US-02, US-03, US-04, US-05, US-06, US-09, US-10).
 * El campo {@code usuarioId} se guarda de forma desnormalizada (copiado del evento dueño)
 * para poder consultar rápidamente "todas las gestiones del usuario" en la vista Hoy.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tareas_logisticas")
public class TareaLogisticaModel {

    /** Estado inicial de toda tarea logística. */
    public static final String ESTADO_PENDIENTE = "Pendiente";
    /** Estado cuando la gestión ya se ejecutó. */
    public static final String ESTADO_COMPLETADA = "Completada";
    /** Estado cuando la gestión se pospuso. */
    public static final String ESTADO_POSPUESTA = "Pospuesta";

    /** Identificador interno autoincremental de la tarea. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Identificador del evento al que pertenece esta tarea logística. */
    @Column(name = "evento_id", nullable = false)
    private Long eventoId;

    /** Identificador del usuario dueño (copiado del evento) para consultas rápidas de la vista Hoy. */
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    /** Nombre de la gestión (ej. "Reservar salón", "Enviar invitaciones"). */
    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    /** Nota o detalle libre de la gestión (opcional). */
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    /** Fecha objetivo/plazo en la que debe realizarse la gestión. */
    @Column(name = "fecha_limite", nullable = false)
    private LocalDate fechaLimite;

    /** Horas estimadas de esfuerzo para completar la gestión. */
    @Column(name = "horas_estimadas", nullable = false, precision = 4, scale = 2)
    private BigDecimal horasEstimadas;

    /** Estado de la gestión: Pendiente, Completada o Pospuesta. */
    @Column(name = "estado", length = 30)
    private String estado;

    /** Fecha en la que se marcó como completada o pospuesta. */
    @Column(name = "fecha_ejecucion")
    private LocalDate fechaEjecucion;

    /** Nota opcional explicativa registrada al marcar la gestión como ejecutada o pospuesta (US-09). */
    @Column(name = "nota_ejecucion", columnDefinition = "TEXT")
    private String notaEjecucion;

    /** Fecha y hora de creación del registro. */
    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

}
