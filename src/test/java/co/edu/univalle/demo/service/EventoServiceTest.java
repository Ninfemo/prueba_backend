package co.edu.univalle.demo.service;

import co.edu.univalle.demo.exception.ResourceNotFoundException;
import co.edu.univalle.demo.model.EventoModel;
import co.edu.univalle.demo.repository.EventoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    private EventoService eventoService;

    @BeforeEach
    void setUp() {
        eventoService = new EventoService(eventoRepository);
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private EventoModel buildEvento() {
        return EventoModel.builder()
                .id(1L)
                .usuarioId(10L)
                .nombre("Conferencia de Sistemas")
                .descripcion("Evento académico de ingeniería de sistemas")
                .fechaEvento(LocalDate.of(2026, 10, 15))
                .estado("Planificación")
                .build();
    }

    // ── Crear ─────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Crear evento exitosamente")
    void whenCreateEvento_thenReturnSavedEvento() {
        var evento = buildEvento();

        given(eventoRepository.save(evento)).willReturn(evento);

        EventoModel result = eventoService.crear(evento);

        verify(eventoRepository).save(evento);
        assertEquals(evento, result);
    }

    // ── Actualizar ────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Actualizar evento exitosamente cuando existe")
    void whenUpdateEvento_withExistingId_thenReturnUpdatedEvento() {
        var evento = buildEvento();

        given(eventoRepository.findById(evento.getId()))
                .willReturn(Optional.of(evento));
        given(eventoRepository.save(evento)).willReturn(evento);

        EventoModel result = eventoService.actualizar(evento);

        verify(eventoRepository).save(evento);
        assertEquals(evento, result);
    }

    @Test
    @DisplayName("Lanzar ResourceNotFoundException al actualizar evento inexistente")
    void whenUpdateEvento_withNonExistingId_thenThrowResourceNotFoundException() {
        var evento = buildEvento();

        given(eventoRepository.findById(evento.getId()))
                .willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> eventoService.actualizar(evento));

        verify(eventoRepository, never()).save(evento);
    }

    // ── Eliminar ──────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Eliminar evento exitosamente cuando existe")
    void whenDeleteEvento_withExistingId_thenDeleteFromRepository() {
        var evento = buildEvento();

        given(eventoRepository.findById(evento.getId()))
                .willReturn(Optional.of(evento));

        eventoService.eliminar(evento.getId());

        verify(eventoRepository).deleteById(evento.getId());
    }

    @Test
    @DisplayName("Lanzar ResourceNotFoundException al eliminar evento inexistente")
    void whenDeleteEvento_withNonExistingId_thenThrowResourceNotFoundException() {
        given(eventoRepository.findById(99L))
                .willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> eventoService.eliminar(99L));

        verify(eventoRepository, never()).deleteById(99L);
    }

    // ── Obtener todos ─────────────────────────────────────────────────────────
    @Test
    @DisplayName("Retornar lista de todos los eventos")
    void whenGetAllEventos_thenReturnList() {
        var eventos = List.of(buildEvento());

        given(eventoRepository.findAll()).willReturn(eventos);

        List<EventoModel> result = eventoService.obtenerTodos();

        verify(eventoRepository).findAll();
        assertEquals(eventos, result);
    }

    // ── Obtener por id ────────────────────────────────────────────────────────
    @Test
    @DisplayName("Retornar evento por id cuando existe")
    void whenGetEventoById_withExistingId_thenReturnEvento() {
        var evento = buildEvento();

        given(eventoRepository.findById(evento.getId()))
                .willReturn(Optional.of(evento));

        EventoModel result = eventoService.obtenerPorId(evento.getId());

        assertEquals(evento, result);
    }

    @Test
    @DisplayName("Lanzar ResourceNotFoundException al buscar id inexistente")
    void whenGetEventoById_withNonExistingId_thenThrowResourceNotFoundException() {
        given(eventoRepository.findById(99L))
                .willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> eventoService.obtenerPorId(99L));
    }

    // ── Buscar por nombre ─────────────────────────────────────────────────────
    @Test
    @DisplayName("Retornar eventos que coinciden con el nombre buscado")
    void whenSearchByNombre_thenReturnMatchingEventos() {
        var eventos = List.of(buildEvento());

        given(eventoRepository.findAllByNombreContainingIgnoreCase("Conferencia"))
                .willReturn(eventos);

        List<EventoModel> result = eventoService.buscarPorNombre("Conferencia");

        assertEquals(eventos, result);
    }

    // ── Buscar por usuarioId ──────────────────────────────────────────────────
    @Test
    @DisplayName("Retornar eventos asociados a un usuario específico")
    void whenSearchByUsuarioId_thenReturnEventos() {
        var eventos = List.of(buildEvento());

        given(eventoRepository.findByUsuarioId(10L))
                .willReturn(eventos);

        List<EventoModel> result = eventoService.buscarPorUsuarioId(10L);

        assertEquals(eventos, result);
    }

    // ── Buscar por estado ─────────────────────────────────────────────────────
    @Test
    @DisplayName("Retornar eventos filtrados por estado")
    void whenSearchByEstado_thenReturnEventos() {
        var eventos = List.of(buildEvento());

        given(eventoRepository.findByEstado("Planificación"))
                .willReturn(eventos);

        List<EventoModel> result = eventoService.buscarPorEstado("Planificación");

        assertEquals(eventos, result);
    }
}