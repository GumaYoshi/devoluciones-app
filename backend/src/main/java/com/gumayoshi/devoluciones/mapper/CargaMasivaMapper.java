package com.gumayoshi.devoluciones.mapper;

import com.gumayoshi.devoluciones.dto.CargaMasivaResponse;
import com.gumayoshi.devoluciones.entity.CargaMasiva;

public class CargaMasivaMapper {

    private CargaMasivaMapper() {
    }

    public static CargaMasivaResponse toResponse(
            CargaMasiva carga
    ) {

        return new CargaMasivaResponse(

                carga.getId(),

                carga.getNombreArchivo(),

                carga.getEstado(),

                carga.getTotalRegistros(),

                carga.getRegistrosExitosos(),

                carga.getRegistrosConError(),

                carga.getFechaInicio(),

                carga.getFechaFin()

        );
    }

}