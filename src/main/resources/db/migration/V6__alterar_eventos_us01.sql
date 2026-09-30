-- 6. Campos adicionales de Eventos requeridos por US-01 (V6__alterar_eventos_us01.sql)
ALTER TABLE eventos
    ADD COLUMN tipo VARCHAR(30),
    ADD COLUMN cliente_contacto VARCHAR(150),
    ADD COLUMN lugar VARCHAR(200),
    ADD COLUMN plazo_limite DATE;
