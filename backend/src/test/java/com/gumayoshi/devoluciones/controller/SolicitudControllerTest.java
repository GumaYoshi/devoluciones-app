package com.gumayoshi.devoluciones.controller;

import tools.jackson.databind.json.JsonMapper;
import com.gumayoshi.devoluciones.domain.EstadoSolicitud;
import com.gumayoshi.devoluciones.domain.Moneda;
import com.gumayoshi.devoluciones.domain.OrigenSolicitud;
import com.gumayoshi.devoluciones.dto.CrearSolicitudRequest;
import com.gumayoshi.devoluciones.dto.SolicitudResponse;
import com.gumayoshi.devoluciones.service.SolicitudService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SolicitudController.class)
class SolicitudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private SolicitudService solicitudService;

    @Test
    void debeCrearSolicitudYRetornar201() throws Exception {

        // Arrange
        CrearSolicitudRequest request = new CrearSolicitudRequest(
                "FOL-001",
                "12.345.678-5",
                "Juan Pérez",
                new BigDecimal("15000.00"),
                Moneda.CLP,
                "Banco Estado",
                "123456789",
                "REF-001",
                OrigenSolicitud.MANUAL,
                "felipe"
        );

        SolicitudResponse response = new SolicitudResponse(
                1L,
                "FOL-001",
                "12.345.678-5",
                "Juan Pérez",
                new BigDecimal("15000.00"),
                Moneda.CLP,
                "Banco Estado",
                "123456789",
                "REF-001",
                OrigenSolicitud.MANUAL,
                EstadoSolicitud.BORRADOR,
                0,
                "felipe",
                LocalDateTime.of(2026, 7, 23, 22, 30),
                LocalDateTime.of(2026, 7, 23, 22, 30)
        );

        when(solicitudService.crearSolicitud(any(CrearSolicitudRequest.class)))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(
                        post("/api/solicitudes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.folio").value("FOL-001"))
                .andExpect(jsonPath("$.rutCliente").value("12.345.678-5"))
                .andExpect(jsonPath("$.nombreCliente").value("Juan Pérez"))
                .andExpect(jsonPath("$.monto").value(15000.00))
                .andExpect(jsonPath("$.moneda").value("CLP"))
                .andExpect(jsonPath("$.origen").value("MANUAL"))
                .andExpect(jsonPath("$.estado").value("BORRADOR"))
                .andExpect(jsonPath("$.cantidadReaperturas").value(0));

        verify(solicitudService)
                .crearSolicitud(any(CrearSolicitudRequest.class));
    }

    @Test
    void debeRetornar400CuandoElRutEsInvalido() throws Exception {

        CrearSolicitudRequest request = new CrearSolicitudRequest(
                "FOL-002",
                "12.345.678-9",
                "Juan Pérez",
                new BigDecimal("15000.00"),
                Moneda.CLP,
                "Banco Estado",
                "123456789",
                "REF-002",
                OrigenSolicitud.MANUAL,
                "felipe"
        );

        mockMvc.perform(
                        post("/api/solicitudes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(solicitudService, never())
                .crearSolicitud(any(CrearSolicitudRequest.class));
    }
}