package co.edu.univalle.demo.repository;

import co.edu.univalle.demo.model.HistorialReprogramacionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad HistorialReprogramacionModel.
 * Spring Data genera automáticamente la implementación en tiempo de ejecución.
 */
@Repository
public interface HistorialReprogramacionRepository extends JpaRepository<HistorialReprogramacionModel, Long> {
}
