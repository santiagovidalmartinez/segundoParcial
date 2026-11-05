package co.edu.unicauca.question.infrastructure.jpa.repository;

import co.edu.unicauca.question.infrastructure.jpa.record.QuestionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio de Spring Data: trabaja con la entidad JPA, nunca con el modelo de dominio. */
public interface SpringQuestionDataRepository extends JpaRepository<QuestionRecord, Long> {
}
