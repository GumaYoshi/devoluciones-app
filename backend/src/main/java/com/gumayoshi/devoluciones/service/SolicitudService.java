package com.gumayoshi.devoluciones.service;

import com.gumayoshi.devoluciones.dto.CrearSolicitudRequest;
import com.gumayoshi.devoluciones.dto.SolicitudResponse;

public interface SolicitudService {

    SolicitudResponse crearSolicitud(CrearSolicitudRequest request);
}