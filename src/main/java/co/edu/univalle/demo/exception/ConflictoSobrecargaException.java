package co.edu.univalle.demo.exception;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;

/**
 * Excepción lanzada cuando guardar o reprogramar una tarea logística superaría
 * el límite diario de horas del usuario (US-07). El GlobalExceptionHandler la
 * mapea a HTTP 409 Conflict, incluyendo el detalle necesario para que el
 * frontend ofrezca alternativas de resolución (US-08).
 */
@Getter
public class ConflictoSobrecargaException extends RuntimeException {

    /** Total de horas que quedarían planificadas ese día si se guarda el cambio. */
    private final BigDecimal horasTotales;

    /** Límite diario de horas configurado por el usuario. */
    private final BigDecimal limite;

    /** Opciones sugeridas para resolver el conflicto (US-08). */
    private final List<String> opciones;

    /**
     * Crea la excepción con el detalle completo del conflicto de sobrecarga.
     *
     * @param message      mensaje legible para el usuario
     * @param horasTotales horas totales que quedarían planificadas ese día
     * @param limite       límite diario configurado por el usuario
     * @param opciones     opciones sugeridas para resolver el conflicto
     */
    public ConflictoSobrecargaException(
            final String message,
            final BigDecimal horasTotales,
            final BigDecimal limite,
            final List<String> opciones) {
        super(message);
        this.horasTotales = horasTotales;
        this.limite = limite;
        this.opciones = opciones;
    }
}
