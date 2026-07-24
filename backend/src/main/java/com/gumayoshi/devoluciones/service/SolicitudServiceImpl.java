package com.gumayoshi.devoluciones.service;

import com.gumayoshi.devoluciones.domain.EstadoSolicitud;
import com.gumayoshi.devoluciones.dto.*;
import com.gumayoshi.devoluciones.entity.EventoSolicitud;
import com.gumayoshi.devoluciones.entity.Solicitud;
import com.gumayoshi.devoluciones.exception.BusinessException;
import com.gumayoshi.devoluciones.exception.ResourceNotFoundException;
import com.gumayoshi.devoluciones.mapper.SolicitudMapper;
import com.gumayoshi.devoluciones.repository.EventoSolicitudRepository;
import com.gumayoshi.devoluciones.repository.SolicitudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class SolicitudServiceImpl implements SolicitudService {

    private static final Logger log =
            LoggerFactory.getLogger(SolicitudServiceImpl.class);

    private final SolicitudRepository solicitudRepository;
    private final EventoSolicitudRepository eventoSolicitudRepository;

    public SolicitudServiceImpl(
            SolicitudRepository solicitudRepository,
            EventoSolicitudRepository eventoSolicitudRepository
    ) {
        this.solicitudRepository = solicitudRepository;
        this.eventoSolicitudRepository = eventoSolicitudRepository;
    }

    @Override
    @Transactional
    public SolicitudResponse crearSolicitud(CrearSolicitudRequest request) {

        log.info(
                "Creando solicitud con folio {} y referencia {}",
                request.folio(),
                request.referenciaBanco()
        );
        validarFolioUnico(request.folio());
        validarReferenciaBancoUnica(request.referenciaBanco());

        Solicitud solicitud = SolicitudMapper.toEntity(request);

        Solicitud solicitudGuardada =
                solicitudRepository.save(solicitud);

        log.info(
                "Solicitud creada correctamente. id={}, folio={}",
                solicitudGuardada.getId(),
                solicitudGuardada.getFolio()
        );
        return SolicitudMapper.toResponse(solicitudGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudResponse> obtenerTodas() {

        return solicitudRepository.findAll()
                .stream()
                .map(SolicitudMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SolicitudResponse obtenerPorId(Long id) {

        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe una solicitud con id " + id
                ));

        return SolicitudMapper.toResponse(solicitud);
    }

    @Override
    @Transactional
    public SolicitudResponse cambiarEstado(
            Long id,
            CambiarEstadoRequest request
    ) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe una solicitud con id " + id
                ));

        EstadoSolicitud estadoActual = solicitud.getEstado();
        EstadoSolicitud nuevoEstado = request.nuevoEstado();

        log.info(
                "Cambio de estado solicitado. id={}, estadoActual={}, nuevoEstado={}, usuario={}",
                id,
                estadoActual,
                nuevoEstado,
                request.usuario()
        );

        if (!estadoActual.puedeTransicionarA(nuevoEstado)) {
            log.warn(
                    "Transición inválida para solicitud {}: {} -> {}",
                    id,
                    estadoActual,
                    nuevoEstado
            );
            throw new BusinessException(
                    "No se permite cambiar el estado de "
                            + estadoActual
                            + " a "
                            + nuevoEstado,
                    HttpStatus.CONFLICT

            );
        }

        solicitud.setEstado(nuevoEstado);
        solicitud.setActualizadaPor(request.usuario());

        if (nuevoEstado == EstadoSolicitud.RECHAZADA) {
            solicitud.setMotivoRechazo(request.comentario());
        } else {
            solicitud.setMotivoRechazo(null);
        }

        EventoSolicitud evento = new EventoSolicitud(
                solicitud,
                estadoActual,
                nuevoEstado,
                request.usuario(),
                request.comentario()
        );

        eventoSolicitudRepository.save(evento);

        Solicitud solicitudActualizada =
                solicitudRepository.save(solicitud);

        log.info(
                "Estado actualizado correctamente. id={}, nuevoEstado={}",
                id,
                nuevoEstado
        );
        return SolicitudMapper.toResponse(solicitudActualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventoSolicitudResponse> obtenerHistorial(Long id) {

        if (!solicitudRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "No existe una solicitud con id " + id
            );
        }

        return eventoSolicitudRepository
                .findBySolicitudIdOrderByFechaAsc(id)
                .stream()
                .map(evento -> new EventoSolicitudResponse(
                        evento.getId(),
                        evento.getEstadoOrigen(),
                        evento.getEstadoDestino(),
                        evento.getUsuario(),
                        evento.getFecha(),
                        evento.getComentario()
                ))
                .toList();
    }

    @Override
    @Transactional
    public SolicitudResponse actualizar(

            Long id,
            ActualizarSolicitudRequest request
    ) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe una solicitud con id " + id
                ));

        if (solicitud.getEstado() != EstadoSolicitud.BORRADOR) {
            throw new BusinessException(
                    "Solo se pueden editar solicitudes en estado BORRADOR",
                    HttpStatus.CONFLICT
            );
        }

        solicitud.setRutCliente(request.rutCliente());
        solicitud.setNombreCliente(request.nombreCliente());
        solicitud.setMonto(request.monto());
        solicitud.setMoneda(request.moneda());
        solicitud.setBancoDestino(request.bancoDestino());
        solicitud.setCuentaDestino(request.cuentaDestino());

        Solicitud solicitudActualizada =
                solicitudRepository.save(solicitud);

        log.info("Solicitud {} actualizada correctamente", id);
        return SolicitudMapper.toResponse(solicitudActualizada);
    }

    private void validarFolioUnico(String folio) {

        if (solicitudRepository.existsByFolio(folio)) {
            throw new BusinessException(
                    "Ya existe una solicitud con el folio " + folio,
                    HttpStatus.CONFLICT
            );
        }
    }

    private void validarReferenciaBancoUnica(String referenciaBanco) {

        if (referenciaBanco == null || referenciaBanco.isBlank()) {
            return;
        }

        if (solicitudRepository.existsByReferenciaBanco(referenciaBanco)) {
            throw new BusinessException(
                    "Ya existe una solicitud con la referencia bancaria "
                            + referenciaBanco,
                    HttpStatus.CONFLICT
            );
        }
    }
}