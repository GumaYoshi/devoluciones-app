package com.gumayoshi.devoluciones.service;

import com.gumayoshi.devoluciones.domain.Moneda;
import com.gumayoshi.devoluciones.domain.OrigenSolicitud;
import com.gumayoshi.devoluciones.dto.ActualizarSolicitudRequest;
import com.gumayoshi.devoluciones.dto.CrearSolicitudRequest;
import com.gumayoshi.devoluciones.dto.SolicitudResponse;
import com.gumayoshi.devoluciones.entity.Solicitud;
import com.gumayoshi.devoluciones.exception.BusinessException;
import com.gumayoshi.devoluciones.repository.SolicitudRepository;
import com.gumayoshi.devoluciones.exception.ResourceNotFoundException;
import com.gumayoshi.devoluciones.domain.EstadoSolicitud;
import com.gumayoshi.devoluciones.repository.EventoSolicitudRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;


import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceImplTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private EventoSolicitudRepository eventoSolicitudRepository;

    @InjectMocks
    private SolicitudServiceImpl solicitudService;

    @Test
    void debeCrearSolicitudCorrectamente() {

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

        when(solicitudRepository.existsByFolio("FOL-001"))
                .thenReturn(false);

        when(solicitudRepository.existsByReferenciaBanco("REF-001"))
                .thenReturn(false);

        when(solicitudRepository.save(any(Solicitud.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SolicitudResponse response =
                solicitudService.crearSolicitud(request);

        assertNotNull(response);
        assertEquals("FOL-001", response.folio());
        assertEquals("Juan Pérez", response.nombreCliente());
        assertEquals(new BigDecimal("15000.00"), response.monto());
        assertEquals(Moneda.CLP, response.moneda());
        assertEquals(OrigenSolicitud.MANUAL, response.origen());
        assertEquals(0, response.cantidadReaperturas());

        verify(solicitudRepository).existsByFolio("FOL-001");
        verify(solicitudRepository)
                .existsByReferenciaBanco("REF-001");
        verify(solicitudRepository).save(any(Solicitud.class));

        verifyNoMoreInteractions(solicitudRepository);
    }

    @Test
    void debeLanzarExcepcionCuandoElFolioYaExiste() {

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

        when(solicitudRepository.existsByFolio("FOL-001"))
                .thenReturn(true);

        // Act + Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> solicitudService.crearSolicitud(request)
        );

        assertTrue(exception.getMessage().contains("folio"));

        verify(solicitudRepository).existsByFolio("FOL-001");

        verify(solicitudRepository, never()).save(any());
    }

    @Test
    void debeLanzarExcepcionCuandoLaReferenciaBancoYaExiste() {

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

        when(solicitudRepository.existsByFolio("FOL-001"))
                .thenReturn(false);

        when(solicitudRepository.existsByReferenciaBanco("REF-001"))
                .thenReturn(true);

        // Act + Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> solicitudService.crearSolicitud(request)
        );

        assertTrue(exception.getMessage().contains("referencia"));

        verify(solicitudRepository).existsByFolio("FOL-001");
        verify(solicitudRepository)
                .existsByReferenciaBanco("REF-001");

        verify(solicitudRepository, never()).save(any());
    }

    @Test
    void debeObtenerTodasLasSolicitudes() {

        Solicitud solicitudUno = new Solicitud(
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

        Solicitud solicitudDos = new Solicitud(
                "FOL-002",
                "11.111.111-1",
                "María González",
                new BigDecimal("20000.00"),
                Moneda.CLP,
                "Banco de Chile",
                "987654321",
                "REF-002",
                OrigenSolicitud.MANUAL,
                "felipe"
        );

        when(solicitudRepository.findAll())
                .thenReturn(List.of(solicitudUno, solicitudDos));

        List<SolicitudResponse> respuestas =
                solicitudService.obtenerTodas();

        assertNotNull(respuestas);
        assertEquals(2, respuestas.size());
        assertEquals("FOL-001", respuestas.get(0).folio());
        assertEquals("FOL-002", respuestas.get(1).folio());

        verify(solicitudRepository).findAll();
        verifyNoMoreInteractions(solicitudRepository);
    }

    @Test
    void debeObtenerSolicitudPorId() {

        Solicitud solicitud = new Solicitud(
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

        when(solicitudRepository.findById(1L))
                .thenReturn(Optional.of(solicitud));

        SolicitudResponse response =
                solicitudService.obtenerPorId(1L);

        assertNotNull(response);
        assertEquals("FOL-001", response.folio());
        assertEquals("Juan Pérez", response.nombreCliente());

        verify(solicitudRepository).findById(1L);
        verifyNoMoreInteractions(solicitudRepository);
    }

    @Test
    void debeLanzarExcepcionCuandoLaSolicitudNoExiste() {

        when(solicitudRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> solicitudService.obtenerPorId(999L)
        );

        assertTrue(exception.getMessage().contains("999"));

        verify(solicitudRepository).findById(999L);
        verifyNoMoreInteractions(solicitudRepository);
    }

    @Test
    void debeActualizarSolicitudCuandoEstaEnBorrador() {

        Solicitud solicitud = crearSolicitudEjemplo();

        ActualizarSolicitudRequest request =
                new ActualizarSolicitudRequest(
                        "12.345.678-5",
                        "Juan Pérez Actualizado",
                        new BigDecimal("25000.00"),
                        Moneda.CLP,
                        "Banco de Chile",
                        "999999999"
                );

        when(solicitudRepository.findById(1L))
                .thenReturn(Optional.of(solicitud));

        when(solicitudRepository.save(any(Solicitud.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SolicitudResponse resultado =
                solicitudService.actualizar(1L, request);

        assertEquals(
                "Juan Pérez Actualizado",
                resultado.nombreCliente()
        );

        assertEquals(
                new BigDecimal("25000.00"),
                resultado.monto()
        );

        verify(solicitudRepository).save(solicitud);
    }

    @Test
    void debeRechazarActualizacionCuandoNoEstaEnBorrador() {

        Solicitud solicitud = crearSolicitudEjemplo();
        solicitud.setEstado(EstadoSolicitud.EN_REVISION);

        ActualizarSolicitudRequest request =
                new ActualizarSolicitudRequest(
                        "12.345.678-5",
                        "Juan Pérez",
                        new BigDecimal("25000.00"),
                        Moneda.CLP,
                        "Banco Estado",
                        "123456789"
                );

        when(solicitudRepository.findById(1L))
                .thenReturn(Optional.of(solicitud));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> solicitudService.actualizar(1L, request)
        );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatus()
        );

        assertTrue(
                exception.getMessage().contains("BORRADOR")
        );

        verify(solicitudRepository, never())
                .save(any(Solicitud.class));
    }

    private Solicitud crearSolicitudEjemplo() {

        return new Solicitud(
                "DEV-2026-000001",
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
    }

}