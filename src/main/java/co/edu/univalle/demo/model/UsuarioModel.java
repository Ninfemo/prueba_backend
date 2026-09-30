package co.edu.univalle.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Entidad JPA que representa un usuario organizador en el sistema de gestión logística. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class UsuarioModel {

    /** Identificador interno autoincremental del usuario. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Nombre completo del usuario. */
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    /** Correo electrónico único del usuario. */
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    /**
     * Contraseña del usuario. Al recibirla en un JSON de entrada se acepta en texto plano
     * (el servicio la convierte a hash BCrypt antes de guardar); al serializar una respuesta
     * NUNCA se expone (véase {@code @JsonIgnore} en el getter), incluso si ya está hasheada.
     */
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /** Límite de horas de gestión permitidas por día para evitar sobrecarga (US-12, rango 1-16, por defecto 6). */
    @Column(name = "limite_horas_diarias", precision = 4, scale = 2)
    private BigDecimal limiteHorasDiarias;

    /** Fecha y hora de creación del registro. */
    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Oculta el hash de la contraseña en cualquier respuesta JSON de la API.
     *
     * @return el hash de la contraseña (uso interno únicamente)
     */
    @JsonIgnore
    public String getPasswordHash() {
        return passwordHash;
    }

}
