package com.gumayoshi.devoluciones.dto;

import com.gumayoshi.devoluciones.domain.EstadoSolicitud;

import java.time.LocalDateTime;

public record EventoSolicitudResponse(
        Long id,
        EstadoSolicitud estadoOrigen,
        EstadoSolicitud estadoDestino,
        String usuario,
        LocalDateTime fecha,
        String comentario
) {
}