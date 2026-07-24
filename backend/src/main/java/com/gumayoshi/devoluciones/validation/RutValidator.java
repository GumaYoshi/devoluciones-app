package com.gumayoshi.devoluciones.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RutValidator implements ConstraintValidator<ValidRut, String> {

    @Override
    public boolean isValid(
            String rut,
            ConstraintValidatorContext context
    ) {
        if (rut == null || rut.isBlank()) {
            return true;
        }

        String rutLimpio = rut
                .replace(".", "")
                .replace("-", "")
                .trim()
                .toUpperCase();

        if (rutLimpio.length() < 2) {
            return false;
        }

        String cuerpo = rutLimpio.substring(
                0,
                rutLimpio.length() - 1
        );

        char digitoVerificadorIngresado =
                rutLimpio.charAt(rutLimpio.length() - 1);

        if (!cuerpo.matches("\\d+")) {
            return false;
        }

        char digitoVerificadorCalculado =
                calcularDigitoVerificador(cuerpo);

        return digitoVerificadorIngresado
                == digitoVerificadorCalculado;
    }

    private char calcularDigitoVerificador(String cuerpo) {
        int suma = 0;
        int multiplicador = 2;

        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(
                    cuerpo.charAt(i)
            );

            suma += digito * multiplicador;
            multiplicador++;

            if (multiplicador > 7) {
                multiplicador = 2;
            }
        }

        int resto = 11 - (suma % 11);

        return switch (resto) {
            case 11 -> '0';
            case 10 -> 'K';
            default -> Character.forDigit(resto, 10);
        };
    }
}