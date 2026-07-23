package com.gumayoshi.devoluciones.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstadoSolicitudTest {

    @Test
    void borradorPuedeEnviarseARevision() {
        assertTrue(
                EstadoSolicitud.BORRADOR
                        .puedeTransicionarA(EstadoSolicitud.EN_REVISION)
        );
    }

    @Test
    void borradorPuedeAnularse() {
        assertTrue(
                EstadoSolicitud.BORRADOR
                        .puedeTransicionarA(EstadoSolicitud.ANULADA)
        );
    }

    @Test
    void enRevisionPuedeAprobarse() {
        assertTrue(
                EstadoSolicitud.EN_REVISION
                        .puedeTransicionarA(EstadoSolicitud.APROBADA)
        );
    }

    @Test
    void enRevisionPuedeRechazarse() {
        assertTrue(
                EstadoSolicitud.EN_REVISION
                        .puedeTransicionarA(EstadoSolicitud.RECHAZADA)
        );
    }

    @Test
    void aprobadaPuedePagarse() {
        assertTrue(
                EstadoSolicitud.APROBADA
                        .puedeTransicionarA(EstadoSolicitud.PAGADA)
        );
    }

    @Test
    void rechazadaPuedeReabrirseComoBorrador() {
        assertTrue(
                EstadoSolicitud.RECHAZADA
                        .puedeTransicionarA(EstadoSolicitud.BORRADOR)
        );
    }

    @Test
    void pagadaNoPermiteTransiciones() {
        for (EstadoSolicitud destino : EstadoSolicitud.values()) {
            assertFalse(
                    EstadoSolicitud.PAGADA
                            .puedeTransicionarA(destino)
            );
        }
    }

    @Test
    void anuladaNoPermiteTransiciones() {
        for (EstadoSolicitud destino : EstadoSolicitud.values()) {
            assertFalse(
                    EstadoSolicitud.ANULADA
                            .puedeTransicionarA(destino)
            );
        }
    }

    @Test
    void noPermiteMantenerElMismoEstado() {
        for (EstadoSolicitud estado : EstadoSolicitud.values()) {
            assertFalse(estado.puedeTransicionarA(estado));
        }
    }

    @Test
    void noPermiteDestinoNulo() {
        assertFalse(
                EstadoSolicitud.BORRADOR
                        .puedeTransicionarA(null)
        );
    }
}