package com.gumayoshi.devoluciones.mapper;

import com.gumayoshi.devoluciones.domain.Moneda;
import com.gumayoshi.devoluciones.domain.OrigenSolicitud;
import com.gumayoshi.devoluciones.dto.CrearSolicitudRequest;
import com.gumayoshi.devoluciones.entity.Solicitud;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SolicitudMapperTest {

    @Test
    void debeMapearRequestAEntidad() {

        CrearSolicitudRequest request = new CrearSolicitudRequest(
                "FOL-001",
                "12.345.678-5",
                "Juan Pérez",
                new BigDecimal("10000"),
                Moneda.CLP,
                "Banco Estado",
                "123456",
                "REF-001",
                OrigenSolicitud.MANUAL,
                "felipe"
        );

        Solicitud solicitud = SolicitudMapper.toEntity(request);

        assertEquals("FOL-001", solicitud.getFolio());
        assertEquals("Juan Pérez", solicitud.getNombreCliente());
        assertEquals(Moneda.CLP, solicitud.getMoneda());
        assertEquals(OrigenSolicitud.MANUAL, solicitud.getOrigen());
        assertEquals(0, solicitud.getCantidadReaperturas());
    }
}