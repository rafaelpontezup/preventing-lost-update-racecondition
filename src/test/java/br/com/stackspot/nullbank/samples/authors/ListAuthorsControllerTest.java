package br.com.stackspot.nullbank.samples.authors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class ListAuthorsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private AuthorRespository repository;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
    }

    /**
     * Tip: Try always to begin with the happy-path scenario
     */
    @Test
    @DisplayName("should list all authors")
    public void t1() throws Exception {
        // scenario
        List.of(
            new Author("Rafael Ponte", "rafael.ponte@zup.com.br", LocalDate.now().minusYears(38)),
            new Author("Jordi Silva", "jordi.silva@zup.com.br", LocalDate.now().minusYears(25)),
            new Author("Alberto Souza", "alberto.tavares@zup.com.br", LocalDate.now().minusYears(39))
        ).forEach(author -> {
            repository.save(author);
        });

        // action and validation
        mockMvc.perform(get("/api/v1/authors")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].name").value("Alberto Souza"))
                .andExpect(jsonPath("$[0].email").value("alberto.tavares@zup.com.br"))
                .andExpect(jsonPath("$[1].id").isNotEmpty())
                .andExpect(jsonPath("$[1].name").value("Rafael Ponte"))
                .andExpect(jsonPath("$[1].email").value("rafael.ponte@zup.com.br"))
                .andExpect(jsonPath("$[2].id").isNotEmpty())
                .andExpect(jsonPath("$[2].name").value("Jordi Silva"))
                .andExpect(jsonPath("$[2].email").value("jordi.silva@zup.com.br"))
        ;
    }

    @Test
    @DisplayName("should not list all when there is no authors")
    public void t2() throws Exception {
        // action
        mockMvc.perform(get("/api/v1/authors")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
        ;
    }

}