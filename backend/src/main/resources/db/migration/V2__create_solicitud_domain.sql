CREATE TABLE solicitudes (
                             id BIGSERIAL PRIMARY KEY,

                             folio VARCHAR(20) NOT NULL UNIQUE,

                             rut_cliente VARCHAR(12) NOT NULL,
                             nombre_cliente VARCHAR(150) NOT NULL,

                             monto NUMERIC(12, 2) NOT NULL,
                             moneda VARCHAR(3) NOT NULL DEFAULT 'CLP',

                             banco_destino VARCHAR(100) NOT NULL,
                             cuenta_destino VARCHAR(50) NOT NULL,

                             referencia_banco VARCHAR(100) UNIQUE,

                             origen VARCHAR(30) NOT NULL,
                             estado VARCHAR(30) NOT NULL,

                             motivo_rechazo VARCHAR(500),
                             cantidad_reaperturas SMALLINT NOT NULL DEFAULT 0,

                             creada_por VARCHAR(100) NOT NULL,
                             fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             actualizada_por VARCHAR(100),
                             fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             CONSTRAINT chk_solicitud_monto
                                 CHECK (monto > 0 AND monto <= 10000000),

                             CONSTRAINT chk_solicitud_reaperturas
                                 CHECK (cantidad_reaperturas BETWEEN 0 AND 1),

                             CONSTRAINT chk_solicitud_motivo_rechazo
                                 CHECK (
                                     estado <> 'RECHAZADA'
                                         OR motivo_rechazo IS NOT NULL
                                     )
);

CREATE TABLE eventos_solicitud (
                                   id BIGSERIAL PRIMARY KEY,

                                   solicitud_id BIGINT NOT NULL,

                                   estado_origen VARCHAR(30),
                                   estado_destino VARCHAR(30) NOT NULL,

                                   usuario VARCHAR(100) NOT NULL,
                                   fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   comentario VARCHAR(500),

                                   CONSTRAINT fk_evento_solicitud
                                       FOREIGN KEY (solicitud_id)
                                           REFERENCES solicitudes(id)
);

CREATE INDEX idx_solicitudes_estado
    ON solicitudes(estado);

CREATE INDEX idx_solicitudes_rut
    ON solicitudes(rut_cliente);

CREATE INDEX idx_solicitudes_fecha_creacion
    ON solicitudes(fecha_creacion);

CREATE INDEX idx_solicitudes_origen
    ON solicitudes(origen);

CREATE INDEX idx_eventos_solicitud_id_fecha
    ON eventos_solicitud(solicitud_id, fecha);