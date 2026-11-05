package co.edu.unicauca.question.domain.repository;

import co.edu.unicauca.question.domain.model.Question;

import java.util.List;
import java.util.Optional;

/** Contrato de persistencia que necesita el negocio. No menciona Spring ni JPA (inversion de dependencias). */
public interface QuestionRepository {

    Question save(Question question);

    Optional<Question> findById(Long id);

    List<Question> findAll();
}
