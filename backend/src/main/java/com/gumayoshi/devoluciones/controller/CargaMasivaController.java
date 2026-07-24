package com.gumayoshi.devoluciones.controller;

import com.gumayoshi.devoluciones.dto.CargaMasivaResponse;
import com.gumayoshi.devoluciones.service.CargaMasivaService;
import com.gumayoshi.devoluciones.dto.ErrorCargaResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cargas")
public class CargaMasivaController {

    private final CargaMasivaService cargaMasivaService;

    public CargaMasivaController(
            CargaMasivaService cargaMasivaService
    ) {
        this.cargaMasivaService = cargaMasivaService;
    }

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<CargaMasivaResponse> cargarArchivo(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("usuario") String usuario
    ) {
        CargaMasivaResponse response =
                cargaMasivaService.cargarArchivo(
                        archivo,
                        usuario
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CargaMasivaResponse> obtenerCarga(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                cargaMasivaService.obtenerCarga(id)
        );
    }

    @GetMapping("/{id}/errores")
    public ResponseEntity<List<ErrorCargaResponse>> obtenerErrores(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                cargaMasivaService.obtenerErrores(id)
        );
    }
}