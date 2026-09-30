package co.edu.univalle.demo.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Cuerpo de petición/respuesta para consultar o actualizar el límite diario (US-12). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LimiteDiarioDTO {
    /** Límite diario de horas de gestión, entre 1 y 16. */
    private BigDecimal limiteHorasDiarias;
}
