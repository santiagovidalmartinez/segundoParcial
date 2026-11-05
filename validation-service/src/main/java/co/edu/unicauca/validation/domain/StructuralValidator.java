package co.edu.unicauca.validation.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Reglas de validacion estructural de una pregunta de seleccion multiple (HU03):
 * enunciado obligatorio, exactamente 4 opciones no vacias, clave de respuesta existente (A-D)
 * y justificacion obligatoria. Java puro: no depende de Spring.
 */
public class StructuralValidator {

    public static final int REQUIRED_OPTIONS = 4;
    private static final Set<String> VALID_KEYS = Set.of("A", "B", "C", "D");

    public List<String> validate(String statement, List<String> options, String correctKey, String justification) {
        List<String> errors = new ArrayList<>();

        if (isBlank(statement)) {
            errors.add("El enunciado es obligatorio.");
        }

        if (options == null || options.size() != REQUIRED_OPTIONS) {
            int found = options == null ? 0 : options.size();
            errors.add("La pregunta debe tener exactamente " + REQUIRED_OPTIONS + " opciones (se recibieron " + found + ").");
        } else if (options.stream().anyMatch(StructuralValidator::isBlank)) {
            errors.add("Ninguna de las " + REQUIRED_OPTIONS + " opciones puede estar vacia.");
        }

        if (isBlank(correctKey)) {
            errors.add("La clave de la respuesta correcta es obligatoria.");
        } else if (!VALID_KEYS.contains(correctKey.strip().toUpperCase())) {
            errors.add("La clave '" + correctKey + "' no existe: debe ser A, B, C o D.");
        }

        if (isBlank(justification)) {
            errors.add("La justificacion de la respuesta es obligatoria.");
        }
        return errors;
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
