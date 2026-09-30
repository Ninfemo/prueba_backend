package co.edu.univalle.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Cuerpo de la petición para marcar una tarea logística como ejecutada o pospuesta (US-09). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarcarEstadoRequestDTO {
    /** Nuevo estado: "Completada" o "Pospuesta". */
    private String estado;
    /** Nota opcional explicativa (ej. "esperando confirmación de salón"). */
    private String nota;
}
