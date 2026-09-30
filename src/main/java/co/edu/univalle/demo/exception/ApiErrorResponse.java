package co.edu.univalle.demo.exception;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Estructura estándar de respuesta de error para toda la API (TS-03).
 * Los campos horasTotales/limite/opciones solo se completan para errores
 * de conflicto de sobrecarga (US-07); en el resto de los casos quedan nulos
 * y Jackson simplemente no los incluye en el JSON.
 */
@Getter
@Builder
@AllArgsConstructor
public class ApiErrorResponse {
    /** Código HTTP del error. */
    private int status;
    /** Mensaje descriptivo del error, redactado sin jerga técnica. */
    private String message;
    /** Ruta del endpoint que generó el error. */
    private String path;
    /** Marca de tiempo del momento en que ocurrió el error. */
    private LocalDateTime timestamp;
    /** Horas totales que quedarían planificadas ese día (solo en conflictos de sobrecarga). */
    private BigDecimal horasTotales;
    /** Límite diario configurado por el usuario (solo en conflictos de sobrecarga). */
    private BigDecimal limite;
    /** Opciones sugeridas para resolver un conflicto de sobrecarga (US-08). */
    private List<String> opciones;
}
