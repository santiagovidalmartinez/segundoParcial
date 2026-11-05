package co.edu.unicauca.question.api;

import co.edu.unicauca.question.application.QuestionService;
import co.edu.unicauca.question.application.QuestionServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Prueba la capa REST (201, 400, 202, 404) con servicios falsos: sin Spring Boot ni RabbitMQ. */
class QuestionControllerTest {

    private static final String VALID_BODY = """
            {"statement":"Enunciado","options":["a","b","c","d"],"correctKey":"A","justification":"Porque si"}""";

    private QuestionServiceTest.FakeValidator validator;
    private QuestionServiceTest.FakePublisher publisher;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        validator = new QuestionServiceTest.FakeValidator();
        publisher = new QuestionServiceTest.FakePublisher();
        QuestionService service = new QuestionService(validator, new QuestionServiceTest.InMemoryQuestionRepository(), publisher);
        mvc = MockMvcBuilders.standaloneSetup(new QuestionController(service))
                .setControllerAdvice(new ApiExceptionHandler()).build();
    }

    @Test
    void crearPreguntaValidaResponde201ConEstadoDraft() throws Exception {
        mvc.perform(post("/questions").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/questions/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.options", hasSize(4)));
    }

    @Test
    void crearPreguntaInvalidaResponde400ConLaListaDeErrores() throws Exception {
        validator.errorsToReturn = List.of("La pregunta debe tener exactamente 4 opciones (se recibieron 3).");
        mvc.perform(post("/questions").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("La pregunta debe tener exactamente 4 opciones (se recibieron 3)."));
    }

    @Test
    void enviarARevisionResponde202ConOperationIdYProcessing() throws Exception {
        mvc.perform(post("/questions").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY));
        mvc.perform(post("/questions/1/submit"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.operationId", notNullValue()))
                .andExpect(jsonPath("$.status").value("PROCESSING"));
    }

    @Test
    void enviarARevisionDePreguntaInexistenteResponde404() throws Exception {
        mvc.perform(post("/questions/42/submit")).andExpect(status().isNotFound());
    }

    @Test
    void consultarPreguntaExistente() throws Exception {
        mvc.perform(post("/questions").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY));
        mvc.perform(get("/questions/1")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void jsonMalformadoResponde400() throws Exception {
        mvc.perform(post("/questions").contentType(MediaType.APPLICATION_JSON).content("{no es json"))
                .andExpect(status().isBadRequest());
    }
}
