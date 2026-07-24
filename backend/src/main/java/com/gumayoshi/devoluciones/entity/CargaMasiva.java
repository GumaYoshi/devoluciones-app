package com.gumayoshi.devoluciones.entity;

import com.gumayoshi.devoluciones.domain.EstadoCarga;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cargas_masivas")
public class CargaMasiva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;

    @Column(name = "hash_archivo", nullable = false, unique = true, length = 64)
    private String hashArchivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private EstadoCarga estado;

    @Column(name = "total_registros", nullable = false)
    private Integer totalRegistros;

    @Column(name = "registros_exitosos", nullable = false)
    private Integer registrosExitosos;

    @Column(name = "registros_con_error", nullable = false)
    private Integer registrosConError;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "creada_por", nullable = false, length = 100)
    private String creadaPor;

    public CargaMasiva() {
    }

    public CargaMasiva(
            String nombreArchivo,
            String hashArchivo,
            String creadaPor
    ) {
        this.nombreArchivo = nombreArchivo;
        this.hashArchivo = hashArchivo;
        this.creadaPor = creadaPor;
        this.estado = EstadoCarga.PENDIENTE;
        this.totalRegistros = 0;
        this.registrosExitosos = 0;
        this.registrosConError = 0;
        this.fechaInicio = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getHashArchivo() {
        return hashArchivo;
    }

    public void setHashArchivo(String hashArchivo) {
        this.hashArchivo = hashArchivo;
    }

    public EstadoCarga getEstado() {
        return estado;
    }

    public void setEstado(EstadoCarga estado) {
        this.estado = estado;
    }

    public Integer getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(Integer totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public Integer getRegistrosExitosos() {
        return registrosExitosos;
    }

    public void setRegistrosExitosos(Integer registrosExitosos) {
        this.registrosExitosos = registrosExitosos;
    }

    public Integer getRegistrosConError() {
        return registrosConError;
    }

    public void setRegistrosConError(Integer registrosConError) {
        this.registrosConError = registrosConError;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getCreadaPor() {
        return creadaPor;
    }

    public void setCreadaPor(String creadaPor) {
        this.creadaPor = creadaPor;
    }
}