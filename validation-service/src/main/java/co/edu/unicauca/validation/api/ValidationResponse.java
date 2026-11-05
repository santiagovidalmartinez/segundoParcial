package co.edu.unicauca.validation.api;

import java.util.List;

/** Resultado de la validacion: valid=true con errors vacio, o valid=false con la lista de errores. */
public record ValidationResponse(boolean valid, List<String> errors) {
}
