package co.edu.univalle.demo.security;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Utilidad para obtener el usuario autenticado actual dentro de un controlador.
 * {@link AuthFilter} deja el id del usuario como atributo de la petición una vez
 * validado el token; nunca se confía en un usuarioId enviado por el cliente.
 */
public final class AuthContext {

    /** Nombre del atributo de la petición donde el filtro deja el id del usuario. */
    public static final String ATRIBUTO_USUARIO_ID = "usuarioId";

    private AuthContext() {
    }

    /**
     * Obtiene el id del usuario autenticado a partir de la petición HTTP actual.
     *
     * @param request petición HTTP en curso
     * @return el id del usuario autenticado
     * @throws IllegalStateException si se invoca sobre una ruta no protegida por {@link AuthFilter}
     */
    public static Long usuarioActual(final HttpServletRequest request) {
        final Object valor = request.getAttribute(ATRIBUTO_USUARIO_ID);
        if (!(valor instanceof Long)) {
            throw new IllegalStateException(
                "No hay usuario autenticado en el contexto de la petición");
        }
        return (Long) valor;
    }
}
