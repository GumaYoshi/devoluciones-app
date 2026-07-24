package com.gumayoshi.devoluciones.service;

import com.gumayoshi.devoluciones.domain.EstadoCarga;
import com.gumayoshi.devoluciones.dto.CargaMasivaResponse;
import com.gumayoshi.devoluciones.dto.ErrorCargaResponse;
import com.gumayoshi.devoluciones.entity.CargaMasiva;
import com.gumayoshi.devoluciones.entity.ErrorCarga;
import com.gumayoshi.devoluciones.entity.Solicitud;
import com.gumayoshi.devoluciones.exception.BusinessException;
import com.gumayoshi.devoluciones.exception.ResourceNotFoundException;
import com.gumayoshi.devoluciones.repository.CargaMasivaRepository;
import com.gumayoshi.devoluciones.repository.ErrorCargaRepository;
import com.gumayoshi.devoluciones.repository.SolicitudRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CargaMasivaServiceImplTest {

    private static final String ENCABEZADO =
            "rut_cliente;nombre_cliente;monto;"
                    + "banco_destino;cuenta_destino;referencia_banco";

    @Mock
    private CargaMasivaRepository cargaMasivaRepository;

    @Mock
    private ErrorCargaRepository errorCargaRepository;

    @Mock
    private SolicitudRepository solicitudRepository;

    private ValidatorFactory validatorFactory;

    private CargaMasivaServiceImpl cargaMasivaService;

    @BeforeEach
    void setUp() {
        validatorFactory =
                Validation.buildDefaultValidatorFactory();

        Validator validator = validatorFactory.getValidator();

        cargaMasivaService = new CargaMasivaServiceImpl(
                cargaMasivaRepository,
                errorCargaRepository,
                solicitudRepository,
                validator,
                50000
        );
    }

    @Test
    void debeProcesarArchivoValidoCorrectamente() {
        String contenido = ENCABEZADO + "\n"
                + "12345678-5;JUAN PEREZ;150000.00;"
                + "BANCO ESTADO;123456789;"
                + "REF-2026-000001\n";

        MockMultipartFile archivo = crearArchivo(contenido);

        when(cargaMasivaRepository.existsByHashArchivo(anyString()))
                .thenReturn(false);

        when(cargaMasivaRepository.save(any(CargaMasiva.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(solicitudRepository.existsByFolio(
                "DEV-2026-000001"
        )).thenReturn(false);

        when(solicitudRepository.existsByReferenciaBanco(
                "REF-2026-000001"
        )).thenReturn(false);

        when(solicitudRepository.save(any(Solicitud.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CargaMasivaResponse response =
                cargaMasivaService.cargarArchivo(
                        archivo,
                        "felipe"
                );

        assertNotNull(response);
        assertEquals(EstadoCarga.COMPLETADA, response.estado());
        assertEquals(1, response.totalRegistros());
        assertEquals(1, response.registrosExitosos());
        assertEquals(0, response.registrosConError());
        assertNotNull(response.fechaInicio());
        assertNotNull(response.fechaFin());

        ArgumentCaptor<Solicitud> captor =
                ArgumentCaptor.forClass(Solicitud.class);

        verify(solicitudRepository).save(captor.capture());

        Solicitud solicitudGuardada = captor.getValue();

        assertEquals(
                "DEV-2026-000001",
                solicitudGuardada.getFolio()
        );

        assertEquals(
                "REF-2026-000001",
                solicitudGuardada.getReferenciaBanco()
        );

        assertEquals(
                "felipe",
                solicitudGuardada.getCreadaPor()
        );

        assertEquals(
                "CARGA_MASIVA",
                solicitudGuardada.getOrigen().name()
        );

        verify(errorCargaRepository, never())
                .save(any(ErrorCarga.class));
    }

    @Test
    void debeCompletarCargaConErroresSinDetenerProceso() {
        String contenido = ENCABEZADO + "\n"
                + "12345678-5;JUAN PEREZ;150000.00;"
                + "BANCO ESTADO;123456789;"
                + "REF-2026-000001\n"
                + "12345678-5;ANA TORRES;-5000.00;"
                + "ITAU;987654321;"
                + "REF-2026-000002\n";

        MockMultipartFile archivo = crearArchivo(contenido);

        when(cargaMasivaRepository.existsByHashArchivo(anyString()))
                .thenReturn(false);

        when(cargaMasivaRepository.save(any(CargaMasiva.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(solicitudRepository.existsByFolio(
                "DEV-2026-000001"
        )).thenReturn(false);

        when(solicitudRepository.existsByReferenciaBanco(
                "REF-2026-000001"
        )).thenReturn(false);

        when(solicitudRepository.save(any(Solicitud.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(errorCargaRepository.save(any(ErrorCarga.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CargaMasivaResponse response =
                cargaMasivaService.cargarArchivo(
                        archivo,
                        "felipe"
                );

        assertEquals(
                EstadoCarga.COMPLETADA_CON_ERRORES,
                response.estado()
        );

        assertEquals(2, response.totalRegistros());
        assertEquals(1, response.registrosExitosos());
        assertEquals(1, response.registrosConError());

        verify(solicitudRepository, times(1))
                .save(any(Solicitud.class));

        ArgumentCaptor<ErrorCarga> captor =
                ArgumentCaptor.forClass(ErrorCarga.class);

        verify(errorCargaRepository).save(captor.capture());

        ErrorCarga error = captor.getValue();

        assertEquals(3, error.getNumeroFila());

        assertTrue(
                error.getMensajeError()
                        .contains("mayor que cero")
        );
    }

    @Test
    void debeRechazarArchivoDuplicado() {
        MockMultipartFile archivo = crearArchivo(
                ENCABEZADO + "\n"
                        + "12345678-5;JUAN PEREZ;150000;"
                        + "BANCO ESTADO;123456789;"
                        + "REF-2026-000001"
        );

        when(cargaMasivaRepository.existsByHashArchivo(anyString()))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> cargaMasivaService.cargarArchivo(
                        archivo,
                        "felipe"
                )
        );

        assertTrue(
                exception.getMessage()
                        .contains("procesado anteriormente")
        );

        verify(cargaMasivaRepository, never())
                .save(any(CargaMasiva.class));

        verify(solicitudRepository, never())
                .save(any(Solicitud.class));
    }

    @Test
    void debeRechazarArchivoVacio() {
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "archivo.csv",
                "text/csv",
                new byte[0]
        );

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> cargaMasivaService.cargarArchivo(
                        archivo,
                        "felipe"
                )
        );

        assertTrue(
                exception.getMessage()
                        .contains("obligatorio")
        );

        verifyNoInteractions(cargaMasivaRepository);
        verifyNoInteractions(solicitudRepository);
        verifyNoInteractions(errorCargaRepository);
    }

    @Test
    void debeMarcarCargaComoFallidaCuandoEncabezadoEsIncorrecto() {
        String contenido =
                "rut;nombre;monto;banco;cuenta;referencia\n"
                        + "12345678-5;JUAN PEREZ;150000;"
                        + "BANCO ESTADO;123456789;"
                        + "REF-2026-000001";

        MockMultipartFile archivo = crearArchivo(contenido);

        when(cargaMasivaRepository.existsByHashArchivo(anyString()))
                .thenReturn(false);

        when(cargaMasivaRepository.save(any(CargaMasiva.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> cargaMasivaService.cargarArchivo(
                        archivo,
                        "felipe"
                )
        );

        assertTrue(
                exception.getMessage()
                        .contains("encabezado")
        );

        ArgumentCaptor<CargaMasiva> captor =
                ArgumentCaptor.forClass(CargaMasiva.class);

        verify(cargaMasivaRepository, times(2))
                .save(captor.capture());

        CargaMasiva cargaFinal =
                captor.getAllValues().get(1);

        assertEquals(
                EstadoCarga.FALLIDA,
                cargaFinal.getEstado()
        );

        assertNotNull(cargaFinal.getFechaFin());

        verifyNoInteractions(solicitudRepository);
        verifyNoInteractions(errorCargaRepository);
    }

    @Test
    void debeObtenerCargaPorId() {
        CargaMasiva carga = new CargaMasiva(
                "pagos.csv",
                "abc123",
                "felipe"
        );

        carga.setEstado(EstadoCarga.COMPLETADA);
        carga.setTotalRegistros(10);
        carga.setRegistrosExitosos(10);
        carga.setRegistrosConError(0);

        when(cargaMasivaRepository.findById(1L))
                .thenReturn(Optional.of(carga));

        CargaMasivaResponse response =
                cargaMasivaService.obtenerCarga(1L);

        assertNotNull(response);
        assertEquals("pagos.csv", response.nombreArchivo());
        assertEquals(10, response.totalRegistros());
        assertEquals(10, response.registrosExitosos());
        assertEquals(0, response.registrosConError());
        assertEquals(EstadoCarga.COMPLETADA, response.estado());

        verify(cargaMasivaRepository).findById(1L);
    }

    @Test
    void debeObtenerErroresOrdenadosDeUnaCarga() {
        CargaMasiva carga = new CargaMasiva(
                "pagos.csv",
                "abc123",
                "felipe"
        );

        ErrorCarga primerError = new ErrorCarga(
                carga,
                18,
                "fila 18",
                "El monto debe ser mayor que cero"
        );

        ErrorCarga segundoError = new ErrorCarga(
                carga,
                37,
                "fila 37",
                "La cuenta destino es obligatoria"
        );

        when(cargaMasivaRepository.existsById(1L))
                .thenReturn(true);

        when(
                errorCargaRepository
                        .findByCargaMasivaIdOrderByNumeroFilaAsc(1L)
        ).thenReturn(List.of(primerError, segundoError));

        List<ErrorCargaResponse> errores =
                cargaMasivaService.obtenerErrores(1L);

        assertNotNull(errores);
        assertEquals(2, errores.size());

        assertEquals(18, errores.get(0).numeroFila());
        assertEquals(37, errores.get(1).numeroFila());

        assertEquals(
                "El monto debe ser mayor que cero",
                errores.get(0).mensajeError()
        );

        verify(cargaMasivaRepository).existsById(1L);

        verify(errorCargaRepository)
                .findByCargaMasivaIdOrderByNumeroFilaAsc(1L);
    }

    @Test
    void debeLanzarExcepcionAlConsultarErroresDeCargaInexistente() {
        when(cargaMasivaRepository.existsById(9999L))
                .thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> cargaMasivaService.obtenerErrores(9999L)
        );

        assertTrue(
                exception.getMessage().contains("9999")
        );

        verify(cargaMasivaRepository).existsById(9999L);

        verifyNoInteractions(errorCargaRepository);
    }

    private MockMultipartFile crearArchivo(String contenido) {
        return new MockMultipartFile(
                "archivo",
                "pagos_banco_ejemplo.csv",
                "text/csv",
                contenido.getBytes(StandardCharsets.UTF_8)
        );
    }
}