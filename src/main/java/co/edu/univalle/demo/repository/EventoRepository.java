package co.edu.univalle.demo.repository;

import co.edu.univalle.demo.model.EventoModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad EventoModel.
 * Spring Data genera automáticamente la implementación en tiempo de ejecución.
 */
@Repository
public interface EventoRepository extends JpaRepository<EventoModel, Long> {

    /**
     * Busca eventos cuyo nombre contenga el texto indicado.
     * 
     * @param nombre fragmento del nombre a buscar
     * @return lista de eventos que coinciden
     */
    List<EventoModel> findAllByNombreContainingIgnoreCase(String nombre);

    /**
     * Busca todos los eventos asociados a un usuario organizador específico.
     * 
     * @param usuarioId identificador único del usuario
     * @return lista de eventos creados por el usuario
     */
    List<EventoModel> findByUsuarioId(Long usuarioId);

    /**
     * Busca eventos filtrando por su estado actual.
     * 
     * @param estado estado del evento (ej. Planificación, En Progreso, etc.)
     * @return lista de eventos con el estado indicado
     */
    List<EventoModel> findByEstado(String estado);

}