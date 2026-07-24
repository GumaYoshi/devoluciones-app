package com.gumayoshi.devoluciones.repository;

import com.gumayoshi.devoluciones.entity.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    boolean existsByFolio(String folio);

    boolean existsByReferenciaBanco(String referenciaBanco);

    Optional<Solicitud> findByFolio(String folio);

}