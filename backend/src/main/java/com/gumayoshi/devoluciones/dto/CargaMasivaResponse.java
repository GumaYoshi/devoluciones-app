package com.gumayoshi.devoluciones.dto;

import com.gumayoshi.devoluciones.domain.EstadoCarga;

import java.time.LocalDateTime;

public record CargaMasivaResponse(

        Long id,

        String nombreArchivo,

        EstadoCarga estado,

        Integer totalRegistros,

        Integer registrosExitosos,

        Integer registrosConError,

        LocalDateTime fechaInicio,

        LocalDateTime fechaFin

) {
}