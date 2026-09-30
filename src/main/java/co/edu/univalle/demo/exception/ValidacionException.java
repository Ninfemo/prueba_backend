package co.edu.univalle.demo.exception;

/**
 * Excepción lanzada cuando los datos de entrada no cumplen las reglas de validación.
 * El GlobalExceptionHandler la mapea a HTTP 400 Bad Request.
 */
public class ValidacionException extends RuntimeException {
    /**
     * Crea la excepción con un mensaje descriptivo.
     *
     * @param message descripción del error de validación
     */
    public ValidacionException(final String message) {
        super(message);
    }
}
