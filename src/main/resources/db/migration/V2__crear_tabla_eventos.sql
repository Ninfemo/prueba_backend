-- 2. Tabla de Eventos (V2__crear_tabla_eventos.sql)
CREATE TABLE eventos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_evento DATE NOT NULL,
    estado VARCHAR(50) DEFAULT 'Planificación',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_evento_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);