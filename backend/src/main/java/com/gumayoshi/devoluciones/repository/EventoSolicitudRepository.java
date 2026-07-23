package com.gumayoshi.devoluciones.repository;

import com.gumayoshi.devoluciones.entity.EventoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoSolicitudRepository
        extends JpaRepository<EventoSolicitud, Long> {

    List<EventoSolicitud>
    findBySolicitudIdOrderByFechaAsc(Long solicitudId);
}