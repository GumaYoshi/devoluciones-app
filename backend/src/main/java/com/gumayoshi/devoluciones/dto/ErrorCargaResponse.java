package com.gumayoshi.devoluciones.dto;

public record ErrorCargaResponse(

        Long id,

        Integer numeroFila,

        String contenidoFila,

        String mensajeError

) {
}