package co.edu.unicauca.question.infrastructure.jpa.repository;

import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.repository.QuestionRepository;
import co.edu.unicauca.question.infrastructure.jpa.mapper.QuestionDomainMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementacion del contrato de dominio QuestionRepository sobre SpringQuestionDataRepository (H2).
 * Convierte con QuestionDomainMapper y mantiene la lectura dentro de la transaccion (las opciones son LAZY).
 */
@Repository
public class QuestionRepositoryJpaImpl implements QuestionRepository {

    private final SpringQuestionDataRepository data;

    public QuestionRepositoryJpaImpl(SpringQuestionDataRepository data) {
        this.data = data;
    }

    @Override
    @Transactional
    public Question save(Question question) {
        return QuestionDomainMapper.toDomain(data.save(QuestionDomainMapper.toRecord(question)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Question> findById(Long id) {
        return data.findById(id).map(QuestionDomainMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findAll() {
        return data.findAll().stream().map(QuestionDomainMapper::toDomain).toList();
    }
}
