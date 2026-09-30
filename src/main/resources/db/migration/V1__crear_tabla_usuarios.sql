-- 1. Tabla de Usuarios (V1__crear_tabla_usuarios.sql)
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    limite_horas_diarias DECIMAL(4,2) DEFAULT 6.00,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON COLUMN usuarios.limite_horas_diarias IS 'Límite por defecto para evitar sobrecarga diaria (ej. 6 horas)';