package com.gumayoshi.devoluciones.service;

import com.gumayoshi.devoluciones.dto.*;

import java.util.List;

public interface SolicitudService {

    SolicitudResponse crearSolicitud(CrearSolicitudRequest request);

    List<SolicitudResponse> obtenerTodas();

    SolicitudResponse obtenerPorId(Long id);

    SolicitudResponse cambiarEstado(
            Long id,
            CambiarEstadoRequest request
    );

    List<EventoSolicitudResponse> obtenerHistorial(Long id);

    SolicitudResponse actualizar(
            Long id,
            ActualizarSolicitudRequest request
    );
}