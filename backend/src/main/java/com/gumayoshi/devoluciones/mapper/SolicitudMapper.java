package com.gumayoshi.devoluciones.mapper;

import com.gumayoshi.devoluciones.dto.CrearSolicitudRequest;
import com.gumayoshi.devoluciones.dto.SolicitudResponse;
import com.gumayoshi.devoluciones.entity.Solicitud;

public final class SolicitudMapper {

    private SolicitudMapper() {
    }

    public static Solicitud toEntity(CrearSolicitudRequest request) {
        return new Solicitud(
                request.folio(),
                request.rutCliente(),
                request.nombreCliente(),
                request.monto(),
                request.moneda(),
                request.bancoDestino(),
                request.cuentaDestino(),
                request.referenciaBanco(),
                request.origen(),
                request.creadaPor()
        );
    }

    public static SolicitudResponse toResponse(Solicitud solicitud) {
        return new SolicitudResponse(
                solicitud.getId(),
                solicitud.getFolio(),
                solicitud.getRutCliente(),
                solicitud.getNombreCliente(),
                solicitud.getMonto(),
                solicitud.getMoneda(),
                solicitud.getBancoDestino(),
                solicitud.getCuentaDestino(),
                solicitud.getReferenciaBanco(),
                solicitud.getOrigen(),
                solicitud.getEstado(),
                solicitud.getCantidadReaperturas(),
                solicitud.getCreadaPor(),
                solicitud.getFechaCreacion(),
                solicitud.getFechaActualizacion()
        );
    }
}