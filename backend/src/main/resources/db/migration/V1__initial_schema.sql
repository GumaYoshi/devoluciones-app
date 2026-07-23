CREATE TABLE usuarios (
                          id BIGSERIAL PRIMARY KEY,
                          username VARCHAR(100) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL,
                          rol VARCHAR(30) NOT NULL,
                          activo BOOLEAN NOT NULL DEFAULT TRUE,
                          fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);