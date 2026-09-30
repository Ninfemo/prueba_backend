package co.edu.univalle.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA que registra cada intento de reprogramación de una tarea logística,
 * haya generado conflicto de sobrecarga o no (US-06, US-07, US-08).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "historial_reprogramaciones")
public class HistorialReprogramacionModel {

    /** Identificador interno autoincremental del registro de historial. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Identificador de la tarea logística reprogramada. */
    @Column(name = "tarea_id", nullable = false)
    private Long tareaId;

    /** Fecha que tenía la tarea antes del intento de reprogramación. */
    @Column(name = "fecha_anterior", nullable = false)
    private LocalDate fechaAnterior;

    /** Fecha nueva solicitada para la tarea. */
    @Column(name = "fecha_nueva", nullable = false)
    private LocalDate fechaNueva;

    /** Indica si el intento de reprogramación generó un conflicto de sobrecarga diaria. */
    @Column(name = "conflicto_detectado")
    private Boolean conflictoDetectado;

    /** Detalle legible del conflicto detectado (nulo si no hubo conflicto). */
    @Column(name = "detalle_conflicto", columnDefinition = "TEXT")
    private String detalleConflicto;

    /** Fecha y hora en la que se registró el intento de reprogramación. */
    @Column(name = "fecha_reprogramacion", insertable = false, updatable = false)
    private LocalDateTime fechaReprogramacion;

}
