package com.gumayoshi.devoluciones.repository;

import com.gumayoshi.devoluciones.entity.ErrorCarga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ErrorCargaRepository
        extends JpaRepository<ErrorCarga, Long> {

    List<ErrorCarga> findByCargaMasivaIdOrderByNumeroFilaAsc(
            Long cargaMasivaId
    );
}