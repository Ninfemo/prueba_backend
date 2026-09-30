-- 3. Tabla de Historial de Reprogramaciones y Conflictos (V4__crear_tabla_historial_reprogramaciones.sql)
CREATE TABLE historial_reprogramaciones (
    id BIGSERIAL PRIMARY KEY,
    tarea_id INT NOT NULL,
    fecha_anterior DATE NOT NULL,
    fecha_nueva DATE NOT NULL,
    conflicto_detectado BOOLEAN DEFAULT FALSE,
    detalle_conflicto TEXT NULL,
    fecha_reprogramacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_historial_tarea FOREIGN KEY (tarea_id) REFERENCES tareas_logisticas(id) ON DELETE CASCADE
);