package co.edu.univalle.demo.dto;

import co.edu.univalle.demo.model.TareaLogisticaModel;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Respuesta de la vista "Hoy" (US-04, US-05): agrupa las gestiones logísticas
 * del usuario en Vencidas, Para hoy y Próximas, con la regla de orden documentada.
 */
@Getter
@AllArgsConstructor
public class HoyResponseDTO {
    /** Texto de la regla de agrupación y orden, visible en la interfaz (US-04). */
    private String regla;
    /** Gestiones cuyo plazo ya pasó, de la más antigua a la más reciente. */
    private List<TareaLogisticaModel> vencidas;
    /** Gestiones cuyo plazo es hoy. */
    private List<TareaLogisticaModel> paraHoy;
    /** Gestiones próximas (dentro de los siguientes 7 días), de la más cercana a la más lejana. */
    private List<TareaLogisticaModel> proximas;
}
