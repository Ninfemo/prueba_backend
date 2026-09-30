package co.edu.univalle.demo.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Registra {@link AuthFilter} para que se aplique únicamente a las rutas de la API. */
@Configuration
public class FilterConfig {

    /**
     * Registra el filtro de autenticación restringido a {@code /api/*}.
     *
     * @param tokenService servicio de validación de tokens
     * @return el registro del filtro
     */
    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterRegistration(final TokenService tokenService) {
        final FilterRegistrationBean<AuthFilter> registro = new FilterRegistrationBean<>();
        registro.setFilter(new AuthFilter(tokenService));
        registro.addUrlPatterns("/api/*");
        registro.setOrder(1);
        return registro;
    }
}
