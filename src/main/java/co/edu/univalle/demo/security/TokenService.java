package co.edu.univalle.demo.security;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Servicio de autenticación mínima (TS-04): genera y valida tokens firmados
 * con HMAC-SHA256, sin depender de librerías externas de JWT. El token tiene
 * la forma {@code payloadBase64.firmaBase64}, donde el payload contiene
 * {@code usuarioId:expiraEnEpochSegundos}.
 */
@Component
public class TokenService {

    /** Algoritmo usado para firmar el token. */
    private static final String ALGORITMO = "HmacSHA256";

    /** Clave secreta para firmar y validar tokens. */
    private final String secreto;

    /** Horas de validez del token desde su emisión. */
    private final long expiracionHoras;

    /**
     * Constructor con inyección de configuración.
     *
     * @param secreto         clave secreta configurada en {@code app.jwt.secret}
     * @param expiracionHoras horas de validez configuradas en {@code app.jwt.expiracion-horas}
     */
    public TokenService(
            @Value("${app.jwt.secret}") final String secreto,
            @Value("${app.jwt.expiracion-horas:24}") final long expiracionHoras) {
        this.secreto = secreto;
        this.expiracionHoras = expiracionHoras;
    }

    /**
     * Genera un token firmado para el usuario indicado.
     *
     * @param usuarioId identificador del usuario autenticado
     * @return token en formato {@code payloadBase64.firmaBase64}
     */
    public String generarToken(final Long usuarioId) {
        final long expiraEn = Instant.now().plusSeconds(expiracionHoras * 3600).getEpochSecond();
        final String payload = usuarioId + ":" + expiraEn;
        final String payloadB64 = base64(payload);
        final String firma = firmar(payloadB64);
        return payloadB64 + "." + firma;
    }

    /**
     * Valida un token y, si es válido y no ha expirado, retorna el id del usuario.
     *
     * @param token token recibido en el header Authorization
     * @return el id del usuario si el token es válido; {@code null} en caso contrario
     */
    public Long validarYObtenerUsuarioId(final String token) {
        if (token == null || !token.contains(".")) {
            return null;
        }
        final String[] partes = token.split("\\.", 2);
        if (partes.length != 2) {
            return null;
        }
        final String payloadB64 = partes[0];
        final String firmaRecibida = partes[1];
        final String firmaEsperada = firmar(payloadB64);
        if (!firmaEsperada.equals(firmaRecibida)) {
            return null;
        }
        try {
            final String payload = new String(Base64.getUrlDecoder().decode(payloadB64), StandardCharsets.UTF_8);
            final String[] campos = payload.split(":");
            if (campos.length != 2) {
                return null;
            }
            final long expiraEn = Long.parseLong(campos[1]);
            if (Instant.now().getEpochSecond() > expiraEn) {
                return null;
            }
            return Long.parseLong(campos[0]);
        } catch (final RuntimeException ex) {
            return null;
        }
    }

    /**
     * Codifica un texto en Base64 URL-safe sin relleno.
     *
     * @param texto texto a codificar
     * @return texto codificado
     */
    private String base64(final String texto) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(texto.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Calcula la firma HMAC-SHA256 de un texto usando la clave secreta configurada.
     *
     * @param texto texto a firmar
     * @return firma en Base64 URL-safe sin relleno
     */
    private String firmar(final String texto) {
        try {
            final Mac mac = Mac.getInstance(ALGORITMO);
            mac.init(new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), ALGORITMO));
            final byte[] firmaBytes = mac.doFinal(texto.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(firmaBytes);
        } catch (final NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new IllegalStateException("No fue posible firmar el token", ex);
        }
    }
}
