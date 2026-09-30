-- Tabla de Tareas Logísticas (Subtareas del plan de trabajo)
CREATE TABLE tareas_logisticas (
    id BIGSERIAL PRIMARY KEY,
    evento_id INT NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_limite DATE NOT NULL,
    horas_estimadas DECIMAL(4,2) NOT NULL DEFAULT 1.00,
    estado VARCHAR(30) DEFAULT 'Pendiente',
    es_urgente_hoy BOOLEAN DEFAULT FALSE,
    fecha_ejecucion DATE NULL,
    nota_ejecucion TEXT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tarea_evento FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE
);

-- Comentarios de columnas compatibles con PostgreSQL
COMMENT ON COLUMN tareas_logisticas.estado IS 'Pendiente, En Progreso, Completada, Pospuesta';
COMMENT ON COLUMN tareas_logisticas.es_urgente_hoy IS 'Indicador para la vista Hoy';