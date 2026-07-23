package com.gumayoshi.devoluciones.entity;

import com.gumayoshi.devoluciones.domain.EstadoSolicitud;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "eventos_solicitud")
public class EventoSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitud_id", nullable = false)
    private Solicitud solicitud;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_origen", length = 30)
    private EstadoSolicitud estadoOrigen;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_destino", nullable = false, length = 30)
    private EstadoSolicitud estadoDestino;

    @Column(nullable = false, length = 100)
    private String usuario;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(length = 500)
    private String comentario;

    protected EventoSolicitud() {
        // Requerido por JPA
    }

    public EventoSolicitud(
            Solicitud solicitud,
            EstadoSolicitud estadoOrigen,
            EstadoSolicitud estadoDestino,
            String usuario,
            String comentario
    ) {
        this.solicitud = solicitud;
        this.estadoOrigen = estadoOrigen;
        this.estadoDestino = estadoDestino;
        this.usuario = usuario;
        this.comentario = comentario;
        this.fecha = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Solicitud getSolicitud() {
        return solicitud;
    }

    public EstadoSolicitud getEstadoOrigen() {
        return estadoOrigen;
    }

    public EstadoSolicitud getEstadoDestino() {
        return estadoDestino;
    }

    public String getUsuario() {
        return usuario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getComentario() {
        return comentario;
    }
}