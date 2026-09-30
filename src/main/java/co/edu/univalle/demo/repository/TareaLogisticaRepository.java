package co.edu.univalle.demo.repository;

import co.edu.univalle.demo.model.TareaLogisticaModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad TareaLogisticaModel.
 * Spring Data genera automáticamente la implementación en tiempo de ejecución.
 */
@Repository
public interface TareaLogisticaRepository extends JpaRepository<TareaLogisticaModel, Long> {

    /**
     * Busca todas las tareas logísticas de un evento específico.
     *
     * @param eventoId identificador del evento
     * @return lista de tareas logísticas del evento
     */
    List<TareaLogisticaModel> findByEventoId(Long eventoId);

    /**
     * Busca todas las tareas logísticas de todos los eventos de un usuario (vista Hoy).
     *
     * @param usuarioId identificador del usuario dueño
     * @return lista de tareas logísticas del usuario
     */
    List<TareaLogisticaModel> findByUsuarioId(Long usuarioId);
}
