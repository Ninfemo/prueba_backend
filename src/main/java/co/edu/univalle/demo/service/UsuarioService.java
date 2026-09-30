package co.edu.univalle.demo.service;

import co.edu.univalle.demo.exception.BusinessException;
import co.edu.univalle.demo.exception.ResourceNotFoundException;
import co.edu.univalle.demo.exception.ValidacionException;
import co.edu.univalle.demo.model.UsuarioModel;
import co.edu.univalle.demo.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio que contiene la lógica de negocio para la gestión de usuarios organizadores,
 * incluyendo el hash de contraseñas y la validación del límite diario (US-12).
 */
@Service
public class UsuarioService {

    /** Límite diario mínimo permitido, en horas (US-12). */
    private static final BigDecimal LIMITE_MINIMO = BigDecimal.ONE;

    /** Límite diario máximo permitido, en horas (US-12). */
    private static final BigDecimal LIMITE_MAXIMO = BigDecimal.valueOf(16);

    /** Límite diario por defecto cuando el usuario no ha configurado uno (US-12). */
    private static final BigDecimal LIMITE_POR_DEFECTO = BigDecimal.valueOf(6);

    /** Repositorio de acceso a datos de usuarios. */
    private final UsuarioRepository usuarioRepository;

    /** Codificador de contraseñas usado para nunca guardar texto plano. */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Constructor con inyección de dependencias.
     *
     * @param usuarioRepository repositorio de usuarios
     */
    public UsuarioService(final UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Crea un nuevo usuario validando la unicidad de su correo electrónico.
     * La contraseña recibida en texto plano se convierte a hash BCrypt antes de guardar.
     *
     * @param usuario datos del usuario a crear (con la contraseña en texto plano en passwordHash)
     * @return el usuario creado con su id asignado
     * @throws BusinessException si el email ya está registrado
     */
    @Transactional
    public UsuarioModel crear(final UsuarioModel usuario) {
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new ValidacionException("El correo electrónico es obligatorio");
        }
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new ValidacionException("El nombre es obligatorio");
        }
        if (usuario.getPasswordHash() == null || usuario.getPasswordHash().isBlank()) {
            throw new ValidacionException("La contraseña es obligatoria");
        }
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new BusinessException(
                "Ya existe un usuario con el email: " + usuario.getEmail()
            );
        }
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));
        usuario.setLimiteHorasDiarias(validarOAplicarLimitePorDefecto(usuario.getLimiteHorasDiarias()));
        return usuarioRepository.save(usuario);
    }

    /**
     * Actualiza los datos de un usuario existente. Si se envía una contraseña nueva,
     * se vuelve a hashear; si se deja igual a la anterior, no se re-hashea dos veces.
     *
     * @param usuario datos actualizados del usuario con id válido
     * @return el usuario actualizado
     * @throws ResourceNotFoundException si el usuario no existe
     */
    @Transactional
    public UsuarioModel actualizar(final UsuarioModel usuario) {
        final UsuarioModel existente = usuarioRepository.findById(usuario.getId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Usuario con id " + usuario.getId() + " no encontrado"
            ));
        if (usuario.getPasswordHash() != null && !usuario.getPasswordHash().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));
        } else {
            usuario.setPasswordHash(existente.getPasswordHash());
        }
        usuario.setLimiteHorasDiarias(validarOAplicarLimitePorDefecto(usuario.getLimiteHorasDiarias()));
        return usuarioRepository.save(usuario);
    }

    /**
     * Elimina un usuario por su id.
     *
     * @param id identificador del usuario a eliminar
     * @throws ResourceNotFoundException si el usuario no existe
     */
    @Transactional
    public void eliminar(final Long id) {
        usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Usuario con id " + id + " no encontrado"
            ));
        usuarioRepository.deleteById(id);
    }

    /**
     * Retorna todos los usuarios registrados.
     *
     * @return lista de usuarios
     */
    @Transactional(readOnly = true)
    public List<UsuarioModel> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Busca un usuario por su id.
     *
     * @param id identificador del usuario
     * @return el usuario encontrado
     * @throws ResourceNotFoundException si el usuario no existe
     */
    @Transactional(readOnly = true)
    public UsuarioModel obtenerPorId(final Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Usuario con id " + id + " no encontrado"
            ));
    }

    /**
     * Busca usuarios cuyo nombre contenga el texto indicado.
     *
     * @param nombre fragmento del nombre a buscar
     * @return lista de usuarios que coinciden
     */
    @Transactional(readOnly = true)
    public List<UsuarioModel> buscarPorNombre(final String nombre) {
        return usuarioRepository.findAllByNombreContainingIgnoreCase(nombre);
    }

    /**
     * Busca un usuario por su email.
     *
     * @param email correo electrónico del usuario
     * @return Optional con el usuario si existe
     */
    @Transactional(readOnly = true)
    public Optional<UsuarioModel> buscarPorEmail(final String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Verifica credenciales de acceso (US-11). Nunca revela si el problema fue el
     * correo o la contraseña, y tolera de forma segura hashes con formato inválido
     * (por ejemplo, cuentas creadas antes de incorporar el hash BCrypt).
     *
     * @param email    correo electrónico ingresado
     * @param password contraseña en texto plano ingresada
     * @return el usuario si las credenciales son correctas; Optional vacío en caso contrario
     */
    @Transactional(readOnly = true)
    public Optional<UsuarioModel> verificarCredenciales(final String email, final String password) {
        final Optional<UsuarioModel> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isEmpty()) {
            return Optional.empty();
        }
        try {
            if (passwordEncoder.matches(password, usuario.get().getPasswordHash())) {
                return usuario;
            }
        } catch (final IllegalArgumentException ex) {
            // El hash almacenado no tiene formato BCrypt válido (cuenta creada antes
            // de incorporar el hash). Se trata como credenciales inválidas, no como error.
            return Optional.empty();
        }
        return Optional.empty();
    }

    /**
     * Obtiene el límite diario de horas configurado por un usuario (US-12).
     *
     * @param usuarioId identificador del usuario
     * @return el límite configurado, o el valor por defecto (6h) si no ha configurado ninguno
     */
    @Transactional(readOnly = true)
    public BigDecimal obtenerLimiteDiario(final Long usuarioId) {
        final UsuarioModel usuario = obtenerPorId(usuarioId);
        return usuario.getLimiteHorasDiarias() != null
                ? usuario.getLimiteHorasDiarias()
                : LIMITE_POR_DEFECTO;
    }

    /**
     * Actualiza el límite diario de horas de un usuario, validando el rango permitido (US-12).
     *
     * @param usuarioId identificador del usuario
     * @param nuevoLimite nuevo límite diario en horas
     * @return el límite ya guardado
     * @throws ValidacionException si el valor está fuera del rango 1-16
     */
    @Transactional
    public BigDecimal actualizarLimiteDiario(final Long usuarioId, final BigDecimal nuevoLimite) {
        final UsuarioModel usuario = obtenerPorId(usuarioId);
        usuario.setLimiteHorasDiarias(validarOAplicarLimitePorDefecto(nuevoLimite));
        usuarioRepository.save(usuario);
        return usuario.getLimiteHorasDiarias();
    }

    /**
     * Valida que el límite esté en el rango permitido (1-16), o retorna el valor
     * por defecto (6h) si no se envió ninguno.
     *
     * @param limite límite propuesto (puede ser nulo)
     * @return el límite validado, o el valor por defecto
     * @throws ValidacionException si el valor está fuera del rango permitido
     */
    private BigDecimal validarOAplicarLimitePorDefecto(final BigDecimal limite) {
        if (limite == null) {
            return LIMITE_POR_DEFECTO;
        }
        if (limite.compareTo(LIMITE_MINIMO) < 0 || limite.compareTo(LIMITE_MAXIMO) > 0) {
            throw new ValidacionException(
                "El límite diario debe estar entre " + LIMITE_MINIMO + " y " + LIMITE_MAXIMO + " horas");
        }
        return limite;
    }

}
