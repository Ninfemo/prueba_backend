package co.edu.univalle.demo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import co.edu.univalle.demo.exception.ApiErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * Filtro que protege las rutas de negocio de la API (US-11, TS-04): exige un
 * token válido en el header {@code Authorization: Bearer <token>} para todo lo
 * que no esté explícitamente en la lista pública, y deja el id del usuario
 * autenticado disponible para los controladores mediante {@link AuthContext}.
 */
public class AuthFilter extends HttpFilter {

    /** Servicio de validación de tokens. */
    private final TokenService tokenService;

    /** Serializador JSON para las respuestas de error del propio filtro. */
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    /**
     * Constructor con inyección de dependencias.
     *
     * @param tokenService servicio de tokens
     */
    public AuthFilter(final TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * Intercepta cada petición a {@code /api/*} y valida el token salvo en las rutas públicas.
     *
     * @param request  petición HTTP entrante
     * @param response respuesta HTTP saliente
     * @param chain    cadena de filtros
     * @throws IOException      si ocurre un error de E/S
     * @throws ServletException si ocurre un error del contenedor de servlets
     */
    @Override
    protected void doFilter(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain chain) throws IOException, ServletException {

        if (esRutaPublica(request)) {
            chain.doFilter(request, response);
            return;
        }

        final String header = request.getHeader("Authorization");
        final String token = (header != null && header.startsWith("Bearer "))
                ? header.substring("Bearer ".length())
                : null;

        final Long usuarioId = tokenService.validarYObtenerUsuarioId(token);
        if (usuarioId == null) {
            responderNoAutenticado(request, response);
            return;
        }

        request.setAttribute(AuthContext.ATRIBUTO_USUARIO_ID, usuarioId);
        chain.doFilter(request, response);
    }

    /**
     * Determina si la ruta solicitada no requiere autenticación.
     *
     * @param request petición HTTP entrante
     * @return {@code true} si la ruta es pública
     */
    private boolean esRutaPublica(final HttpServletRequest request) {
        final String path = request.getServletPath();
        final String metodo = request.getMethod();
        // Las peticiones de preflight CORS nunca llevan Authorization: deben pasar
        // sin exigir token, o el navegador jamás vería la respuesta real.
        if ("OPTIONS".equalsIgnoreCase(metodo)) {
            return true;
        }
        if (path.equals("/api/auth/login")) {
            return true;
        }
        // El alta de usuarios (registro) es pública; el resto de /api/usuarios requiere sesión.
        return path.equals("/api/usuarios") && "POST".equalsIgnoreCase(metodo);
    }

    /**
     * Escribe una respuesta 401 con el mismo formato estándar de error de la API.
     *
     * @param request  petición HTTP entrante
     * @param response respuesta HTTP saliente
     * @throws IOException si ocurre un error de E/S al escribir la respuesta
     */
    private void responderNoAutenticado(
            final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        final ApiErrorResponse cuerpo = ApiErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("No autenticado. Inicia sesión para continuar.")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        response.getWriter().write(objectMapper.writeValueAsString(cuerpo));
    }
}
