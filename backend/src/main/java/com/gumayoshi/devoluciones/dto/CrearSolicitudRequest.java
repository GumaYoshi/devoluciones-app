package com.gumayoshi.devoluciones.dto;

import com.gumayoshi.devoluciones.domain.Moneda;
import com.gumayoshi.devoluciones.domain.OrigenSolicitud;
import com.gumayoshi.devoluciones.validation.ValidRut;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CrearSolicitudRequest(

        @NotBlank(message = "El folio es obligatorio")
        @Pattern(
                regexp = "^DEV-\\d{4}-\\d{6}$",
                message = "El folio debe tener formato DEV-AAAA-NNNNNN"
        )
        String folio,

        @NotBlank(message = "El RUT es obligatorio")
        @ValidRut
        String rutCliente,

        @NotBlank(message = "El nombre del cliente es obligatorio")
        @Size(
                max = 150,
                message = "El nombre del cliente no puede superar los 150 caracteres"
        )
        String nombreCliente,

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(
                value = "0.01",
                message = "El monto debe ser mayor que cero"
        )
        @DecimalMax(
                value = "10000000.00",
                message = "El monto no puede superar los 10.000.000 CLP"
        )
        BigDecimal monto,

        @NotNull(message = "La moneda es obligatoria")
        Moneda moneda,

        @NotBlank(message = "El banco destino es obligatorio")
        @Size(
                max = 100,
                message = "El banco destino no puede superar los 100 caracteres"
        )
        String bancoDestino,

        @NotBlank(message = "La cuenta destino es obligatoria")
        @Size(
                max = 50,
                message = "La cuenta destino no puede superar los 50 caracteres"
        )
        String cuentaDestino,

        @NotBlank(message = "La referencia bancaria es obligatoria")
        @Size(
                max = 100,
                message = "La referencia bancaria no puede superar los 100 caracteres"
        )
        String referenciaBanco,

        @NotNull(message = "El origen es obligatorio")
        OrigenSolicitud origen,

        @NotBlank(message = "El usuario creador es obligatorio")
        @Size(
                max = 100,
                message = "El usuario creador no puede superar los 100 caracteres"
        )
        String creadaPor

) {
}