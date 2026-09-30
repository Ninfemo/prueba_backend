package co.edu.univalle.demo.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Cuerpo de la petición para reprogramar la fecha de una tarea logística (US-06). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReprogramarRequestDTO {
    /** Nueva fecha objetivo propuesta para la gestión. */
    private LocalDate nuevoPlazo;
}
