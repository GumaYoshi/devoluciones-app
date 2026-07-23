package com.gumayoshi.devoluciones.domain;

import java.util.Set;

public enum EstadoSolicitud {
    BORRADOR,
    EN_REVISION,
    APROBADA,
    RECHAZADA,
    PAGADA,
    ANULADA;

    public boolean puedeTransicionarA(EstadoSolicitud destino) {
        if (destino == null){
            return false;
        }

        return switch (this){
            case BORRADOR -> Set.of(EN_REVISION, ANULADA).contains(destino);
            case EN_REVISION -> Set.of(APROBADA, RECHAZADA).contains(destino);
            case RECHAZADA -> destino == BORRADOR;
            case APROBADA -> destino == PAGADA;
            case PAGADA, ANULADA -> false;
        };
    }
}
