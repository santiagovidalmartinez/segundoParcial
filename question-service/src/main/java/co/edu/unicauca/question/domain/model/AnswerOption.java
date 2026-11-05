package co.edu.unicauca.question.domain.model;

/** Opcion de respuesta de una pregunta: etiqueta (A-D) y texto. Java puro, sin anotaciones de persistencia. */
public record AnswerOption(String label, String text) {
}
