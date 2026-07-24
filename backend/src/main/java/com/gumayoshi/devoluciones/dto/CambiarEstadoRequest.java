package com.gumayoshi.devoluciones.dto;

import com.gumayoshi.devoluciones.domain.EstadoSolicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CambiarEstadoRequest(

        @NotNull
        EstadoSolicitud nuevoEstado,

        @NotBlank
        @Size(max = 100)
        String usuario,

        @Size(max = 500)
        String comentario

) {
}