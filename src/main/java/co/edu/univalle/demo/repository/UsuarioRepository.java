package co.edu.univalle.demo.repository;

import co.edu.univalle.demo.model.UsuarioModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad UsuarioModel.
 * Spring Data genera automáticamente la implementación en tiempo de ejecución.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    /**
     * Busca usuarios cuyo nombre contenga el texto indicado.
     * 
     * @param nombre fragmento del nombre a buscar
     * @return lista de usuarios que coinciden
     */
    List<UsuarioModel> findAllByNombreContainingIgnoreCase(String nombre);

    /**
     * Busca un usuario por su correo electrónico.
     * 
     * @param email correo electrónico a buscar
     * @return el usuario si existe
     */
    Optional<UsuarioModel> findByEmail(String email);

}