package com.gumayoshi.devoluciones.service;

import com.gumayoshi.devoluciones.domain.Moneda;
import com.gumayoshi.devoluciones.domain.OrigenSolicitud;
import com.gumayoshi.devoluciones.dto.CrearSolicitudRequest;
import com.gumayoshi.devoluciones.dto.SolicitudResponse;
import com.gumayoshi.devoluciones.entity.Solicitud;
import com.gumayoshi.devoluciones.exception.BusinessException;
import com.gumayoshi.devoluciones.repository.SolicitudRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceImplTest {

    @Mock
    private SolicitudRepository solicitudRepository;

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
}