package com.gumayoshi.devoluciones.dto;

import com.gumayoshi.devoluciones.domain.Moneda;
import com.gumayoshi.devoluciones.validation.ValidRut;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ActualizarSolicitudRequest(

        @NotBlank(message = "El RUT es obligatorio")
        @ValidRut
        String rutCliente,

        @NotBlank(message = "El nombre del cliente es obligatorio")
        @Size(max = 150)
        String nombreCliente,

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01")
        @DecimalMax(value = "10000000.00")
        BigDecimal monto,

        @NotNull(message = "La moneda es obligatoria")
        Moneda moneda,

        @NotBlank(message = "El banco destino es obligatorio")
        @Size(max = 100)
        String bancoDestino,

        @NotBlank(message = "La cuenta destino es obligatoria")
        @Size(max = 50)
        String cuentaDestino

) {
}