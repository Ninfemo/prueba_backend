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

/** Entidad JPA que representa un evento en el sistema de gestión logística (US-01). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "eventos")
public class EventoModel {

    /** Identificador interno autoincremental del evento. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Identificador del usuario organizador del evento (se asigna desde la sesión, nunca desde el cliente). */
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    /** Nombre o título del evento. */
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    /** Tipo de evento (boda, social, corporativo, cumpleaños, otro). */
    @Column(name = "tipo", length = 30)
    private String tipo;

    /** Nombre o dato de contacto del cliente para quien se organiza el evento. */
    @Column(name = "cliente_contacto", length = 150)
    private String clienteContacto;

    /** Descripción detallada del evento (opcional). */
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    /** Fecha programada para la realización del evento. */
    @Column(name = "fecha_evento", nullable = false)
    private LocalDate fechaEvento;

    /** Lugar donde se realizará el evento. */
    @Column(name = "lugar", length = 200)
    private String lugar;

    /** Plazo límite para tener toda la logística lista. */
    @Column(name = "plazo_limite")
    private LocalDate plazoLimite;

    /** Estado actual del evento. */
    @Column(name = "estado", length = 50)
    private String estado;

    /** Fecha y hora de creación del registro. */
    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

}
