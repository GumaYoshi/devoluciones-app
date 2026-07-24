package com.gumayoshi.devoluciones.dto;

import com.gumayoshi.devoluciones.domain.Moneda;
import com.gumayoshi.devoluciones.domain.OrigenSolicitud;
import com.gumayoshi.devoluciones.validation.ValidRut;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CrearSolicitudRequest(

        @NotBlank(message = "El folio es obligatorio")
        @Size(max = 20)
        String folio,

        @NotBlank(message = "El RUT es obligatorio")
        @ValidRut
        String rutCliente,

        @NotBlank(message = "El nombre del cliente es obligatorio")
        @Size(max = 150)
        String nombreCliente,

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero")
        BigDecimal monto,

        @NotNull(message = "La moneda es obligatoria")
        Moneda moneda,

        @NotBlank(message = "El banco destino es obligatorio")
        @Size(max = 100)
        String bancoDestino,

        @NotBlank(message = "La cuenta destino es obligatoria")
        @Size(max = 50)
        String cuentaDestino,

        @Size(max = 100)
        String referenciaBanco,

        @NotNull(message = "El origen es obligatorio")
        OrigenSolicitud origen,

        @NotBlank(message = "El usuario creador es obligatorio")
        @Size(max = 100)
        String creadaPor

) {
}