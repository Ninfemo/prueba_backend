-- 5. Correcciones de tipos y trazabilidad por usuario en tareas logisticas
--    (V5__corregir_tipos_y_usuario_tareas.sql)
--
-- V3 declaro evento_id/tarea_id como INT pero eventos.id/tareas_logisticas.id
-- son BIGSERIAL (bigint). Se amplian los tipos para evitar overflow e
-- inconsistencias con el resto del modelo (todas las FK del sistema usan bigint).
ALTER TABLE tareas_logisticas
    ALTER COLUMN evento_id TYPE BIGINT;

ALTER TABLE historial_reprogramaciones
    ALTER COLUMN tarea_id TYPE BIGINT;

-- Se agrega usuario_id desnormalizado (copiado del evento dueño) para poder
-- consultar de forma eficiente "todas mis gestiones" en la vista Hoy (US-04)
-- sin tener que hacer join contra eventos en cada consulta.
ALTER TABLE tareas_logisticas
    ADD COLUMN usuario_id BIGINT;

UPDATE tareas_logisticas t
    SET usuario_id = e.usuario_id
    FROM eventos e
    WHERE t.evento_id = e.id;

ALTER TABLE tareas_logisticas
    ALTER COLUMN usuario_id SET NOT NULL;

ALTER TABLE tareas_logisticas
    ADD CONSTRAINT fk_tarea_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE;

CREATE INDEX idx_tareas_usuario_fecha ON tareas_logisticas (usuario_id, fecha_limite);
