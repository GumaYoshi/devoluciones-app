package com.gumayoshi.devoluciones.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "errores_carga")
public class ErrorCarga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carga_masiva_id", nullable = false)
    private CargaMasiva cargaMasiva;

    @Column(name = "numero_fila", nullable = false)
    private Integer numeroFila;

    @Column(name = "contenido_fila", columnDefinition = "TEXT")
    private String contenidoFila;

    @Column(name = "mensaje_error", nullable = false, columnDefinition = "TEXT")
    private String mensajeError;

    public ErrorCarga() {
    }

    public ErrorCarga(
            CargaMasiva cargaMasiva,
            Integer numeroFila,
            String contenidoFila,
            String mensajeError
    ) {
        this.cargaMasiva = cargaMasiva;
        this.numeroFila = numeroFila;
        this.contenidoFila = contenidoFila;
        this.mensajeError = mensajeError;
    }

    public Long getId() {
        return id;
    }

    public CargaMasiva getCargaMasiva() {
        return cargaMasiva;
    }

    public void setCargaMasiva(CargaMasiva cargaMasiva) {
        this.cargaMasiva = cargaMasiva;
    }

    public Integer getNumeroFila() {
        return numeroFila;
    }

    public void setNumeroFila(Integer numeroFila) {
        this.numeroFila = numeroFila;
    }

    public String getContenidoFila() {
        return contenidoFila;
    }

    public void setContenidoFila(String contenidoFila) {
        this.contenidoFila = contenidoFila;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }
}