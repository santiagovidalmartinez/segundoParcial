package co.edu.unicauca.question.domain;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Verifica el aislamiento de la arquitectura limpia: el dominio no importa JPA, Spring ni Jackson. */
class DomainPurityTest {

    private static final Path BASE = Paths.get("src/main/java/co/edu/unicauca/question");

    private static List<Path> javaFiles(String pkg) throws IOException {
        try (Stream<Path> s = Files.walk(BASE.resolve(pkg))) {
            return s.filter(p -> p.toString().endsWith(".java")).collect(Collectors.toList());
        }
    }

    private static void assertNoImports(String pkg, String... forbidden) throws IOException {
        List<Path> files = javaFiles(pkg);
        assertFalse(files.isEmpty(), "No se encontraron archivos en " + pkg);
        for (Path file : files) {
            String text = Files.readString(file);
            for (String f : forbidden) {
                assertFalse(text.contains("import " + f), file.getFileName() + " no debe importar " + f);
            }
        }
    }

    @Test
    void dominioNoTieneAnotacionesDeJpaNiDeSpring() throws IOException {
        assertNoImports("domain", "jakarta.persistence", "javax.persistence", "org.hibernate",
                "org.springframework", "com.fasterxml");
    }

    @Test
    void aplicacionNoConoceJpaNiSpringData() throws IOException {
        assertNoImports("application", "jakarta.persistence", "org.hibernate", "org.springframework.data",
                "org.springframework.amqp", "org.springframework.web");
    }

    @Test
    void apiNoImportaInfraestructura() throws IOException {
        assertNoImports("api", "co.edu.unicauca.question.infrastructure");
    }

    @Test
    void laEntidadJpaVivePorCompletoEnInfraestructura() throws IOException {
        String record = Files.readString(BASE.resolve("infrastructure/jpa/record/QuestionRecord.java"));
        assertTrue(record.contains("@Entity"));
        assertTrue(record.contains("@Table(name = \"questions\")"));
        assertTrue(record.contains("@OneToMany"));
        assertTrue(record.contains("CascadeType.ALL"));
    }
}
