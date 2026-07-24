package com.gumayoshi.devoluciones.controller;

import com.gumayoshi.devoluciones.dto.*;
import com.gumayoshi.devoluciones.service.SolicitudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@RestController
@RequestMapping("/api/v1/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @PostMapping
    public ResponseEntity<SolicitudResponse> crearSolicitud(
            @Valid @RequestBody CrearSolicitudRequest request
    ) {
        SolicitudResponse response =
                solicitudService.crearSolicitud(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<SolicitudResponse>> obtenerTodas() {
        return ResponseEntity.ok(
                solicitudService.obtenerTodas()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudResponse> obtenerPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                solicitudService.obtenerPorId(id)
        );
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<SolicitudResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequest request
    ) {
        return ResponseEntity.ok(
                solicitudService.cambiarEstado(id, request)
        );
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<EventoSolicitudResponse>> obtenerHistorial(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                solicitudService.obtenerHistorial(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<SolicitudResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarSolicitudRequest request
    ) {

        return ResponseEntity.ok(
                solicitudService.actualizar(id, request)
        );
    }
}