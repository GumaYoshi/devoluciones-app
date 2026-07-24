package com.gumayoshi.devoluciones.dto;

import com.gumayoshi.devoluciones.domain.EstadoSolicitud;
import com.gumayoshi.devoluciones.domain.Moneda;
import com.gumayoshi.devoluciones.domain.OrigenSolicitud;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SolicitudResponse(

        Long id,
        String folio,
        String rutCliente,
        String nombreCliente,
        BigDecimal monto,
        Moneda moneda,
        String bancoDestino,
        String cuentaDestino,
        String referenciaBanco,
        OrigenSolicitud origen,
        EstadoSolicitud estado,
        Integer cantidadReaperturas,
        String creadaPor,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion

) {
}