package com.gumayoshi.devoluciones.service;

import com.gumayoshi.devoluciones.dto.CargaMasivaResponse;
import org.springframework.web.multipart.MultipartFile;
import com.gumayoshi.devoluciones.dto.ErrorCargaResponse;

import java.util.List;

public interface CargaMasivaService {

    CargaMasivaResponse cargarArchivo(

            MultipartFile archivo,

            String usuario

    );

    CargaMasivaResponse obtenerCarga(

            Long id

    );

    List<ErrorCargaResponse> obtenerErrores(Long cargaId);

}