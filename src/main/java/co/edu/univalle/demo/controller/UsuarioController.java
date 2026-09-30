package co.edu.univalle.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.univalle.demo.model.UsuarioModel;
import co.edu.univalle.demo.service.UsuarioService;

/**
 * Controlador REST para la gestión de usuarios organizadores.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    /** Servicio de lógica de negocio para usuarios. */
    private final UsuarioService usuarioService;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param usuarioService servicio de usuarios
     */
    public UsuarioController(final UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Crea un nuevo usuario en el sistema.
     * 
     * @param usuario datos del usuario a crear
     * @return el usuario creado con código HTTP 201
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioModel crear(@RequestBody final UsuarioModel usuario) {
        return usuarioService.crear(usuario);
    }

    /**
     * Actualiza los datos de un usuario existente.
     * 
     * @param id identificador del usuario
     * @param usuario datos actualizados
     * @return el usuario actualizado
     */
    @PutMapping("/{id}")
    public UsuarioModel actualizar(
            @PathVariable final Long id,
            @RequestBody final UsuarioModel usuario) {
        usuario.setId(id);
        return usuarioService.actualizar(usuario);
    }

    /**
     * Elimina un usuario por su identificador.
     * 
     * @param id identificador del usuario a eliminar
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable final Long id) {
        usuarioService.eliminar(id);
    }

    /**
     * Obtiene la lista de todos los usuarios registrados.
     * 
     * @return lista de usuarios
     */
    @GetMapping
    public List<UsuarioModel> obtenerTodos() {
        return usuarioService.obtenerTodos();
    }

    /**
     * Busca un usuario por su identificador único.
     * 
     * @param id identificador del usuario
     * @return el usuario encontrado
     */
    @GetMapping("/{id}")
    public UsuarioModel obtenerPorId(@PathVariable final Long id) {
        return usuarioService.obtenerPorId(id);
    }

    /**
     * Busca usuarios que coincidan parcialmente con un nombre.
     * 
     * @param nombre fragmento del nombre a buscar
     * @return lista de usuarios coincidentes
     */
    @GetMapping("/buscar")
    public List<UsuarioModel> buscarPorNombre(@RequestParam final String nombre) {
        return usuarioService.buscarPorNombre(nombre);
    }

    /**
     * Busca un usuario por su correo electrónico exacto.
     * 
     * @param email correo electrónico
     * @return el usuario si existe
     */
    @GetMapping("/email")
    public ResponseEntity<UsuarioModel> buscarPorEmail(@RequestParam final String email) {
        Optional<UsuarioModel> usuario = usuarioService.buscarPorEmail(email);
        return usuario.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}