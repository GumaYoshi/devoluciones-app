package com.gumayoshi.devoluciones.entity;

import com.gumayoshi.devoluciones.domain.EstadoSolicitud;
import com.gumayoshi.devoluciones.domain.Moneda;
import com.gumayoshi.devoluciones.domain.OrigenSolicitud;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "solicitudes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_solicitud_folio",
                        columnNames = "folio"
                ),
                @UniqueConstraint(
                        name = "uk_solicitud_referencia_banco",
                        columnNames = "referencia_banco"
                )
        }
)

public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRutCliente() {
        return rutCliente;
    }

    public void setRutCliente(String rutCliente) {
        this.rutCliente = rutCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }

    public String getBancoDestino() {
        return bancoDestino;
    }

    public void setBancoDestino(String bancoDestino) {
        this.bancoDestino = bancoDestino;
    }

    public String getCuentaDestino() {
        return cuentaDestino;
    }

    public void setCuentaDestino(String cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }

    public String getReferenciaBanco() {
        return referenciaBanco;
    }

    public void setReferenciaBanco(String referenciaBanco) {
        this.referenciaBanco = referenciaBanco;
    }

    public OrigenSolicitud getOrigen() {
        return origen;
    }

    public void setOrigen(OrigenSolicitud origen) {
        this.origen = origen;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public Integer getCantidadReaperturas() {
        return cantidadReaperturas;
    }

    public void setCantidadReaperturas(Integer cantidadReaperturas) {
        this.cantidadReaperturas = cantidadReaperturas;
    }

    public String getCreadaPor() {
        return creadaPor;
    }

    public void setCreadaPor(String creadaPor) {
        this.creadaPor = creadaPor;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getActualizadaPor() {
        return actualizadaPor;
    }

    public void setActualizadaPor(String actualizadaPor) {
        this.actualizadaPor = actualizadaPor;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    @Column(nullable = false, length = 20)
    private String folio;

    @Column(name = "rut_cliente", nullable = false, length = 12)
    private String rutCliente;

    @Column(name = "nombre_cliente", nullable = false, length = 150)
    private String nombreCliente;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private Moneda moneda;

    @Column(name = "banco_destino", nullable = false, length = 100)
    private String bancoDestino;

    @Column(name = "cuenta_destino", nullable = false, length = 50)
    private String cuentaDestino;

    @Column(name = "referencia_banco", nullable = false, length = 100)
    private String referenciaBanco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrigenSolicitud origen;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoSolicitud estado;

    @Column(name = "motivo_rechazo", length = 500)
    private String motivoRechazo;

    @Column(name = "cantidad_reaperturas", nullable = false)
    private Integer cantidadReaperturas;

    @Column(name = "creada_por", nullable = false, length = 100)
    private String creadaPor;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "actualizada_por", length = 100)
    private String actualizadaPor;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    protected Solicitud() {
        // Requerido por JPA
    }

    @PrePersist
    void prePersist() {
        LocalDateTime ahora = LocalDateTime.now();

        if (fechaCreacion == null) {
            fechaCreacion = ahora;
        }

        fechaActualizacion = ahora;

        if (moneda == null) {
            moneda = Moneda.CLP;
        }

        if (estado == null) {
            estado = EstadoSolicitud.BORRADOR;
        }

        if (origen == null) {
            origen = OrigenSolicitud.MANUAL;
        }

        if (cantidadReaperturas == null) {
            cantidadReaperturas = 0;
        }
    }

    @PreUpdate
    void preUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

}
