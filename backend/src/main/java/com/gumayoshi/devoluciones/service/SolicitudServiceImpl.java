package com.gumayoshi.devoluciones.service;

import com.gumayoshi.devoluciones.dto.CrearSolicitudRequest;
import com.gumayoshi.devoluciones.dto.SolicitudResponse;
import com.gumayoshi.devoluciones.entity.Solicitud;
import com.gumayoshi.devoluciones.exception.BusinessException;
import com.gumayoshi.devoluciones.mapper.SolicitudMapper;
import com.gumayoshi.devoluciones.repository.SolicitudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepository solicitudRepository;

    public SolicitudServiceImpl(SolicitudRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }

    @Override
    @Transactional
    public SolicitudResponse crearSolicitud(CrearSolicitudRequest request) {

        validarFolioUnico(request.folio());
        validarReferenciaBancoUnica(request.referenciaBanco());

        Solicitud solicitud = SolicitudMapper.toEntity(request);

        Solicitud solicitudGuardada = solicitudRepository.save(solicitud);

        return SolicitudMapper.toResponse(solicitudGuardada);
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