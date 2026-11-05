package co.edu.unicauca.validation;

import co.edu.unicauca.validation.domain.StructuralValidator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StructuralValidatorTest {

    private final StructuralValidator validator = new StructuralValidator();
    private final List<String> fourOptions = List.of("Uno", "Dos", "Tres", "Cuatro");

    @Test
    void preguntaValidaNoTieneErrores() {
        assertTrue(validator.validate("Enunciado", fourOptions, "B", "Porque si").isEmpty());
    }

    @Test
    void claveEnMinusculaEsValida() {
        assertTrue(validator.validate("Enunciado", fourOptions, "c", "Porque si").isEmpty());
    }

    @Test
    void conTresOpcionesFalla() {
        List<String> errors = validator.validate("Enunciado", List.of("a", "b", "c"), "A", "Porque si");
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("exactamente 4"));
    }

    @Test
    void conCincoOpcionesFalla() {
        assertFalse(validator.validate("Enunciado", List.of("a", "b", "c", "d", "e"), "A", "Porque si").isEmpty());
    }

    @Test
    void sinOpcionesFalla() {
        assertFalse(validator.validate("Enunciado", null, "A", "Porque si").isEmpty());
    }

    @Test
    void opcionVaciaFalla() {
        assertFalse(validator.validate("Enunciado", Arrays.asList("a", " ", "c", "d"), "A", "Porque si").isEmpty());
    }

    @Test
    void claveInexistenteFalla() {
        List<String> errors = validator.validate("Enunciado", fourOptions, "E", "Porque si");
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("no existe"));
    }

    @Test
    void sinClaveFalla() {
        assertFalse(validator.validate("Enunciado", fourOptions, null, "Porque si").isEmpty());
    }

    @Test
    void sinJustificacionFalla() {
        assertFalse(validator.validate("Enunciado", fourOptions, "A", "  ").isEmpty());
    }

    @Test
    void acumulaTodosLosErrores() {
        List<String> errors = validator.validate("", List.of("a"), "Z", null);
        assertEquals(4, errors.size());
    }
}
