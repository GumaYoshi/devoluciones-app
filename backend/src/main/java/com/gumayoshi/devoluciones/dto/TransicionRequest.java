package com.gumayoshi.devoluciones.dto;

import com.gumayoshi.devoluciones.domain.RolUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TransicionRequest(

        @NotBlank(message = "El usuario es obligatorio")
        @Size(max = 100)
        String usuario,

        @NotNull(message = "El rol es obligatorio")
        RolUsuario rol,

        @Size(max = 500)
        String comentario

) {
}