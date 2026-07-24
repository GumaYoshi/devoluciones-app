package com.gumayoshi.devoluciones.mapper;

import com.gumayoshi.devoluciones.dto.ErrorCargaResponse;
import com.gumayoshi.devoluciones.entity.ErrorCarga;

public class ErrorCargaMapper {

    private ErrorCargaMapper() {
    }

    public static ErrorCargaResponse toResponse(
            ErrorCarga errorCarga
    ) {
        return new ErrorCargaResponse(
                errorCarga.getId(),
                errorCarga.getNumeroFila(),
                errorCarga.getContenidoFila(),
                errorCarga.getMensajeError()
        );
    }
}