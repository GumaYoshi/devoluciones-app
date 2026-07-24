package com.gumayoshi.devoluciones.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RutValidatorTest {

    private RutValidator validator;

    @BeforeEach
    void setUp() {
        validator = new RutValidator();
    }

    @Test
    void debeAceptarRutValidoConPuntosYGuion() {
        assertTrue(validator.isValid(
                "12.345.678-5",
                null
        ));
    }

    @Test
    void debeAceptarRutValidoSinFormato() {
        assertTrue(validator.isValid(
                "123456785",
                null
        ));
    }

    @Test
    void debeAceptarDigitoVerificadorKMinuscula() {
        assertTrue(validator.isValid(
                "1.000.005-k",
                null
        ));
    }

    @Test
    void debeRechazarRutConDigitoIncorrecto() {
        assertFalse(validator.isValid(
                "12.345.678-9",
                null
        ));
    }

    @Test
    void debeRechazarRutConCaracteresInvalidos() {
        assertFalse(validator.isValid(
                "12.ABC.678-5",
                null
        ));
    }

    @Test
    void debeRechazarRutDemasiadoCorto() {
        assertFalse(validator.isValid(
                "5",
                null
        ));
    }

    @Test
    void debeAceptarNullParaDelegarObligatoriedad() {
        assertTrue(validator.isValid(
                null,
                null
        ));
    }

    @Test
    void debeAceptarVacioParaDelegarObligatoriedad() {
        assertTrue(validator.isValid(
                " ",
                null
        ));
    }
}