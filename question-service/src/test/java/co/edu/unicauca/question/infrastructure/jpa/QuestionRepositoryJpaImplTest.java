package co.edu.unicauca.question.infrastructure.jpa;

import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.model.QuestionStatus;
import co.edu.unicauca.question.infrastructure.jpa.mapper.QuestionDomainMapper;
import co.edu.unicauca.question.infrastructure.jpa.record.QuestionRecord;
import co.edu.unicauca.question.infrastructure.jpa.repository.QuestionRepositoryJpaImpl;
import co.edu.unicauca.question.infrastructure.jpa.repository.SpringQuestionDataRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(QuestionRepositoryJpaImpl.class)
class QuestionRepositoryJpaImplTest {

    @Autowired
    private QuestionRepositoryJpaImpl repository;

    @Autowired
    private SpringQuestionDataRepository data;

    private Question newQuestion() {
        return Question.draft("Cuanto es 2+2?", List.of("3", "4", "5", "6"), "B", "Porque 2+2 es 4");
    }

    @Test
    void guardaYRecuperaLaPreguntaConSusCuatroOpcionesEnOrden() {
        Question saved = repository.save(newQuestion());
        assertNotNull(saved.getId());

        Question found = repository.findById(saved.getId()).orElseThrow();
        assertEquals("Cuanto es 2+2?", found.getStatement());
        assertEquals(QuestionStatus.DRAFT, found.getStatus());
        assertEquals("B", found.getCorrectKey());
        assertEquals(List.of("3", "4", "5", "6"), found.getOptions().stream().map(o -> o.text()).toList());
        assertEquals(List.of("A", "B", "C", "D"), found.getOptions().stream().map(o -> o.label()).toList());
    }

    @Test
    void elCascadeGuardaLasOpcionesJuntoConLaPregunta() {
        repository.save(newQuestion());
        assertEquals(1, data.count());
        QuestionRecord record = data.findAll().get(0);
        assertEquals(4, record.getOptions().size());
        assertNotNull(record.getId());
    }

    @Test
    void findByIdInexistenteDevuelveVacio() {
        assertTrue(repository.findById(999L).isEmpty());
    }

    @Test
    void findAllDevuelveLasPreguntasGuardadas() {
        repository.save(newQuestion());
        repository.save(newQuestion());
        assertEquals(2, repository.findAll().size());
    }

    @Test
    void elMapeadorEsBidireccional() {
        Question original = newQuestion();
        Question roundTrip = QuestionDomainMapper.toDomain(QuestionDomainMapper.toRecord(original));
        assertEquals(original.getStatement(), roundTrip.getStatement());
        assertEquals(original.getOptions(), roundTrip.getOptions());
        assertEquals(original.getStatus(), roundTrip.getStatus());
        assertEquals(original.getCorrectKey(), roundTrip.getCorrectKey());
    }
}
