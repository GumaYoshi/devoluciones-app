package com.gumayoshi.devoluciones.service;

import com.gumayoshi.devoluciones.domain.EstadoCarga;
import com.gumayoshi.devoluciones.domain.Moneda;
import com.gumayoshi.devoluciones.domain.OrigenSolicitud;
import com.gumayoshi.devoluciones.dto.CargaMasivaResponse;
import com.gumayoshi.devoluciones.dto.CrearSolicitudRequest;
import com.gumayoshi.devoluciones.entity.CargaMasiva;
import com.gumayoshi.devoluciones.entity.ErrorCarga;
import com.gumayoshi.devoluciones.entity.Solicitud;
import com.gumayoshi.devoluciones.exception.BusinessException;
import com.gumayoshi.devoluciones.exception.ResourceNotFoundException;
import com.gumayoshi.devoluciones.mapper.CargaMasivaMapper;
import com.gumayoshi.devoluciones.repository.CargaMasivaRepository;
import com.gumayoshi.devoluciones.repository.ErrorCargaRepository;
import com.gumayoshi.devoluciones.repository.SolicitudRepository;
import com.gumayoshi.devoluciones.dto.ErrorCargaResponse;
import com.gumayoshi.devoluciones.mapper.ErrorCargaMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class CargaMasivaServiceImpl implements CargaMasivaService {

    private static final Logger log =
            LoggerFactory.getLogger(CargaMasivaServiceImpl.class);

    private static final long TAMANO_MAXIMO_ARCHIVO_BYTES =
            10L * 1024L * 1024L;

    private static final String ENCABEZADO_ESPERADO =
            "rut_cliente;nombre_cliente;monto;"
                    + "banco_destino;cuenta_destino;referencia_banco";

    private static final int CANTIDAD_COLUMNAS_ESPERADA = 6;

    private final CargaMasivaRepository cargaMasivaRepository;
    private final ErrorCargaRepository errorCargaRepository;
    private final SolicitudRepository solicitudRepository;
    private final Validator validator;
    private final int maximoFilas;

    public CargaMasivaServiceImpl(
            CargaMasivaRepository cargaMasivaRepository,
            ErrorCargaRepository errorCargaRepository,
            SolicitudRepository solicitudRepository,
            Validator validator,
            @Value("${app.carga.max-filas:50000}") int maximoFilas
    ) {
        this.cargaMasivaRepository = cargaMasivaRepository;
        this.errorCargaRepository = errorCargaRepository;
        this.solicitudRepository = solicitudRepository;
        this.validator = validator;
        this.maximoFilas = maximoFilas;
    }

    @Override
    public CargaMasivaResponse cargarArchivo(
            MultipartFile archivo,
            String usuario
    ) {
        validarArchivo(archivo, usuario);

        byte[] contenidoArchivo = obtenerContenido(archivo);
        String hashArchivo = calcularHash(contenidoArchivo);

        if (cargaMasivaRepository.existsByHashArchivo(hashArchivo)) {
            throw new BusinessException(
                    "Este archivo ya fue procesado anteriormente",
                    HttpStatus.CONFLICT
            );
        }

        String nombreArchivo = archivo.getOriginalFilename() != null
                ? archivo.getOriginalFilename()
                : "archivo.csv";

        CargaMasiva carga = new CargaMasiva(
                nombreArchivo,
                hashArchivo,
                usuario.trim()
        );

        carga.setEstado(EstadoCarga.PROCESANDO);
        carga = cargaMasivaRepository.save(carga);

        try {
            procesarContenido(
                    contenidoArchivo,
                    carga,
                    usuario.trim()
            );

            carga.setFechaFin(LocalDateTime.now());

            if (carga.getRegistrosConError() > 0) {
                carga.setEstado(
                        EstadoCarga.COMPLETADA_CON_ERRORES
                );
            } else {
                carga.setEstado(
                        EstadoCarga.COMPLETADA
                );
            }

            carga = cargaMasivaRepository.save(carga);

            return CargaMasivaMapper.toResponse(carga);

        } catch (BusinessException exception) {
            marcarCargaComoFallida(carga);
            throw exception;

        } catch (Exception exception) {
            marcarCargaComoFallida(carga);

            throw new BusinessException(
                    "No fue posible procesar el archivo CSV",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public CargaMasivaResponse obtenerCarga(Long id) {
        CargaMasiva carga = cargaMasivaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe una carga masiva con id " + id
                ));

        return CargaMasivaMapper.toResponse(carga);
    }

    private void procesarContenido(
            byte[] contenidoArchivo,
            CargaMasiva carga,
            String usuario
    ) {
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                new ByteArrayInputStream(
                                        contenidoArchivo
                                ),
                                StandardCharsets.UTF_8
                        )
                )
        ) {
            String encabezado = reader.readLine();

            validarEncabezado(encabezado);

            String linea;
            int numeroFila = 1;
            int total = 0;
            int exitosos = 0;
            int errores = 0;

            while ((linea = reader.readLine()) != null) {
                numeroFila++;

                if (linea.isBlank()) {
                    continue;
                }

                total++;

                if (total > maximoFilas) {
                    throw new BusinessException(
                            "El archivo supera el máximo permitido de "
                                    + maximoFilas
                                    + " registros",
                            HttpStatus.PAYLOAD_TOO_LARGE
                    );
                }

                try {
                    procesarFila(linea, usuario);
                    exitosos++;

                } catch (Exception exception) {
                    errores++;

                    ErrorCarga errorCarga = new ErrorCarga(
                            carga,
                            numeroFila,
                            linea,
                            obtenerMensajeError(exception)
                    );

                    errorCargaRepository.save(errorCarga);
                }
            }

            carga.setTotalRegistros(total);
            carga.setRegistrosExitosos(exitosos);
            carga.setRegistrosConError(errores);

        } catch (IOException exception) {
            throw new BusinessException(
                    "No fue posible leer el archivo CSV",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @Override
    public List<ErrorCargaResponse> obtenerErrores(Long cargaId) {

        if (!cargaMasivaRepository.existsById(cargaId)) {
            throw new ResourceNotFoundException(
                    "No existe una carga masiva con id " + cargaId
            );
        }

        return errorCargaRepository
                .findByCargaMasivaIdOrderByNumeroFilaAsc(cargaId)
                .stream()
                .map(ErrorCargaMapper::toResponse)
                .toList();
    }

    private void procesarFila(
            String linea,
            String usuario
    ) {
        String[] columnas = linea.split(";", -1);

        if (columnas.length != CANTIDAD_COLUMNAS_ESPERADA) {
            throw new IllegalArgumentException(
                    "La fila debe contener exactamente "
                            + CANTIDAD_COLUMNAS_ESPERADA
                            + " columnas, pero contiene "
                            + columnas.length
            );
        }

        CrearSolicitudRequest request =
                crearRequest(columnas, usuario);

        validarRequest(request);
        validarDuplicados(request);

        Solicitud solicitud = new Solicitud(
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

        try {
            solicitudRepository.save(solicitud);

        } catch (DataIntegrityViolationException exception) {
            throw new IllegalArgumentException(
                    "El folio o la referencia bancaria ya existen"
            );
        }
    }

    private CrearSolicitudRequest crearRequest(
            String[] columnas,
            String usuario
    ) {
        String rutCliente = columnas[0].trim();
        String nombreCliente = columnas[1].trim();
        String montoTexto = columnas[2].trim();
        String bancoDestino = columnas[3].trim();
        String cuentaDestino = columnas[4].trim();
        String referenciaBanco = columnas[5].trim();

        BigDecimal monto = convertirMonto(montoTexto);

        String folio = generarFolio(referenciaBanco);

        return new CrearSolicitudRequest(
                folio,
                rutCliente,
                nombreCliente,
                monto,
                Moneda.CLP,
                bancoDestino,
                cuentaDestino,
                referenciaBanco,
                OrigenSolicitud.CARGA_MASIVA,
                usuario
        );
    }

    private BigDecimal convertirMonto(String montoTexto) {
        if (montoTexto == null || montoTexto.isBlank()) {
            throw new IllegalArgumentException(
                    "El monto es obligatorio"
            );
        }

        try {
            return new BigDecimal(montoTexto);

        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "El monto debe ser un número válido"
            );
        }
    }

    private String generarFolio(String referenciaBanco) {
        if (
                referenciaBanco == null
                        || referenciaBanco.isBlank()
        ) {
            throw new IllegalArgumentException(
                    "La referencia bancaria es obligatoria"
            );
        }

        if (!referenciaBanco.matches(
                "^REF-\\d{4}-\\d{6}$"
        )) {
            throw new IllegalArgumentException(
                    "La referencia bancaria debe tener formato "
                            + "REF-AAAA-NNNNNN"
            );
        }

        return referenciaBanco.replaceFirst(
                "^REF-",
                "DEV-"
        );
    }

    private void validarRequest(
            CrearSolicitudRequest request
    ) {
        Set<ConstraintViolation<CrearSolicitudRequest>> violaciones =
                validator.validate(request);

        if (!violaciones.isEmpty()) {
            String mensaje = violaciones.stream()
                    .map(ConstraintViolation::getMessage)
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining("; "));

            throw new IllegalArgumentException(mensaje);
        }
    }

    private void validarDuplicados(
            CrearSolicitudRequest request
    ) {
        if (solicitudRepository.existsByFolio(
                request.folio()
        )) {
            throw new IllegalArgumentException(
                    "El folio ya existe"
            );
        }

        if (solicitudRepository.existsByReferenciaBanco(
                request.referenciaBanco()
        )) {
            throw new IllegalArgumentException(
                    "La referencia bancaria ya existe"
            );
        }
    }

    private void validarEncabezado(String encabezado) {
        if (encabezado == null) {
            throw new BusinessException(
                    "El archivo CSV no contiene encabezado",
                    HttpStatus.BAD_REQUEST
            );
        }

        String encabezadoNormalizado = encabezado
                .replace("\uFEFF", "")
                .trim()
                .toLowerCase();

        if (!ENCABEZADO_ESPERADO.equals(
                encabezadoNormalizado
        )) {
            throw new BusinessException(
                    "El encabezado del CSV no tiene el formato esperado. "
                            + "Se esperaba: "
                            + ENCABEZADO_ESPERADO,
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validarArchivo(
            MultipartFile archivo,
            String usuario
    ) {
        if (archivo == null || archivo.isEmpty()) {
            throw new BusinessException(
                    "El archivo CSV es obligatorio",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (archivo.getSize() > TAMANO_MAXIMO_ARCHIVO_BYTES) {
            throw new BusinessException(
                    "El archivo no puede superar los 10 MB",
                    HttpStatus.PAYLOAD_TOO_LARGE
            );
        }

        String nombreArchivo = archivo.getOriginalFilename();

        if (
                nombreArchivo == null
                        || !nombreArchivo
                        .toLowerCase()
                        .endsWith(".csv")
        ) {
            throw new BusinessException(
                    "El archivo debe tener extensión .csv",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (usuario == null || usuario.isBlank()) {
            throw new BusinessException(
                    "El usuario es obligatorio",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (usuario.trim().length() > 100) {
            throw new BusinessException(
                    "El usuario no puede superar los 100 caracteres",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private byte[] obtenerContenido(
            MultipartFile archivo
    ) {
        try {
            return archivo.getBytes();

        } catch (IOException exception) {
            throw new BusinessException(
                    "No fue posible leer el archivo",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private String calcularHash(byte[] contenido) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(contenido);
            StringBuilder resultado = new StringBuilder();

            for (byte valor : hash) {
                resultado.append(
                        String.format("%02x", valor)
                );
            }

            return resultado.toString();

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 no se encuentra disponible",
                    exception
            );
        }
    }

    private void marcarCargaComoFallida(
            CargaMasiva carga
    ) {
        carga.setEstado(EstadoCarga.FALLIDA);
        carga.setFechaFin(LocalDateTime.now());
        cargaMasivaRepository.save(carga);
    }

    private String obtenerMensajeError(
            Exception exception
    ) {
        if (
                exception.getMessage() == null
                        || exception.getMessage().isBlank()
        ) {
            return "Error desconocido al procesar la fila";
        }

        return exception.getMessage();
    }
}