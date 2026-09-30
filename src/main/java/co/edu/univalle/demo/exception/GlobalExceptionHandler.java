package co.edu.univalle.demo.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Manejador global de excepciones para toda la API REST (TS-03). */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Maneja recursos no encontrados — devuelve HTTP 404.
     *
     * @param ex      excepción lanzada por el service
     * @param request petición HTTP que originó el error
     * @return respuesta con formato estándar de error
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            final ResourceNotFoundException ex,
            final HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    /**
     * Maneja violaciones de reglas de negocio (ej. email duplicado) — devuelve HTTP 409.
     *
     * @param ex      excepción lanzada por el service
     * @param request petición HTTP que originó el error
     * @return respuesta con formato estándar de error
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(
            final BusinessException ex,
            final HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    /**
     * Maneja datos de entrada inválidos — devuelve HTTP 400.
     *
     * @param ex      excepción lanzada por el service
     * @param request petición HTTP que originó el error
     * @return respuesta con formato estándar de error
     */
    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity<ApiErrorResponse> handleValidacion(
            final ValidacionException ex,
            final HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    /**
     * Maneja accesos a recursos de otro usuario — devuelve HTTP 403.
     *
     * @param ex      excepción lanzada por el service
     * @param request petición HTTP que originó el error
     * @return respuesta con formato estándar de error
     */
    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<ApiErrorResponse> handleAccesoDenegado(
            final AccesoDenegadoException ex,
            final HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiErrorResponse.builder()
                        .status(HttpStatus.FORBIDDEN.value())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    /**
     * Maneja conflictos de sobrecarga diaria (US-07) — devuelve HTTP 409 con
     * el detalle necesario para que el frontend ofrezca alternativas (US-08).
     *
     * @param ex      excepción lanzada por el service
     * @param request petición HTTP que originó el error
     * @return respuesta con formato estándar de error, incluyendo el detalle del conflicto
     */
    @ExceptionHandler(ConflictoSobrecargaException.class)
    public ResponseEntity<ApiErrorResponse> handleConflictoSobrecarga(
            final ConflictoSobrecargaException ex,
            final HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .horasTotales(ex.getHorasTotales())
                        .limite(ex.getLimite())
                        .opciones(ex.getOpciones())
                        .build());
    }

    /**
     * Maneja cualquier excepción no controlada — devuelve HTTP 500.
     *
     * @param ex      excepción no controlada
     * @param request petición HTTP que originó el error
     * @return respuesta con formato estándar de error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneral(
            final Exception ex,
            final HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("Error interno del servidor")
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
