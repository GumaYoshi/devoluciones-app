CREATE TABLE cargas_masivas (
                                id BIGSERIAL PRIMARY KEY,
                                nombre_archivo VARCHAR(255) NOT NULL,
                                hash_archivo VARCHAR(64) NOT NULL UNIQUE,
                                estado VARCHAR(40) NOT NULL,
                                total_registros INTEGER NOT NULL DEFAULT 0,
                                registros_exitosos INTEGER NOT NULL DEFAULT 0,
                                registros_con_error INTEGER NOT NULL DEFAULT 0,
                                fecha_inicio TIMESTAMP NOT NULL,
                                fecha_fin TIMESTAMP,
                                creada_por VARCHAR(100) NOT NULL
);

CREATE TABLE errores_carga (
                               id BIGSERIAL PRIMARY KEY,
                               carga_masiva_id BIGINT NOT NULL,
                               numero_fila INTEGER NOT NULL,
                               contenido_fila TEXT,
                               mensaje_error TEXT NOT NULL,

                               CONSTRAINT fk_error_carga_masiva
                                   FOREIGN KEY (carga_masiva_id)
                                       REFERENCES cargas_masivas(id)
                                       ON DELETE CASCADE
);

CREATE INDEX idx_errores_carga_carga_id
    ON errores_carga(carga_masiva_id);

CREATE INDEX idx_cargas_masivas_estado
    ON cargas_masivas(estado);