package com.gumayoshi.devoluciones.repository;

import com.gumayoshi.devoluciones.entity.CargaMasiva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CargaMasivaRepository
        extends JpaRepository<CargaMasiva, Long> {

    boolean existsByHashArchivo(String hashArchivo);

    Optional<CargaMasiva> findByHashArchivo(String hashArchivo);
}