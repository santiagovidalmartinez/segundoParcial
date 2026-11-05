package co.edu.unicauca.validation;

import co.edu.unicauca.validation.api.ValidationController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ValidationControllerTest {

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new ValidationController()).build();
    }

    @Test
    void preguntaValidaResponde200() throws Exception {
        mvc.perform(post("/validate").contentType(MediaType.APPLICATION_JSON).content("""
                        {"statement":"Enunciado","options":["a","b","c","d"],"correctKey":"A","justification":"Porque si"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void preguntaInvalidaResponde400ConErrores() throws Exception {
        mvc.perform(post("/validate").contentType(MediaType.APPLICATION_JSON).content("""
                        {"statement":"Enunciado","options":["a","b","c"],"correctKey":"A"}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.errors.length()").value(2));
    }
}
