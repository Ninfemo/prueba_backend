package co.edu.univalle.demo.exception;

/**
 * Excepción lanzada cuando un usuario autenticado intenta acceder a un recurso
 * que no le pertenece. El GlobalExceptionHandler la mapea a HTTP 403 Forbidden.
 */
public class AccesoDenegadoException extends RuntimeException {
    /**
     * Crea la excepción con un mensaje descriptivo.
     *
     * @param message descripción del acceso denegado
     */
    public AccesoDenegadoException(final String message) {
        super(message);
    }
}
