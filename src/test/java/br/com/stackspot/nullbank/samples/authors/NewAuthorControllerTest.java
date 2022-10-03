package br.com.stackspot.nullbank.samples.authors;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.zalando.problem.spring.common.MediaTypes;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class NewAuthorControllerTest {

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
    @DisplayName("should create a new author")
    public void t1() throws Exception {
        // scenario
        NewAuthorRequest request = new NewAuthorRequest(
                "Rafael Ponte",
                "rafael.ponte@zup.com.br",
                LocalDate.now().minusYears(38)
        );

        // action (and validation)
        mockMvc.perform(post("/api/v1/authors")
                        .contentType(APPLICATION_JSON)
                        .content(toJson(request))
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "en"))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern("**/api/v1/authors/*"))
        ;

        // validation
        // Tip: Try always to verify the side effects
        assertEquals(1, repository.count());
    }

    @Test
    @DisplayName("should not create a new author when parameters are empty")
    public void t2() throws Exception {
        // scenario
        NewAuthorRequest request = new NewAuthorRequest("", "", null);

        // action (and validation)
        mockMvc.perform(post("/api/v1/authors")
                        .contentType(APPLICATION_JSON)
                        .content(toJson(request))
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "en"))
                .andExpect(status().isBadRequest())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, is(MediaTypes.PROBLEM_VALUE)))
                .andExpect(jsonPath("$.type", is("https://zalando.github.io/problem/constraint-violation")))
                .andExpect(jsonPath("$.title", is("Constraint Violation")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.violations", hasSize(3)))
                .andExpect(jsonPath("$.violations", containsInAnyOrder(
                                violation("name", "must not be empty"),
                                violation("email", "must not be empty"),
                                violation("birthdate", "must not be null")
                        )
                ))
        ;

        // validation
        assertEquals(0, repository.count());
    }

    @Test
    @DisplayName("should not create a new author when parameters are invalid")
    public void t3() throws Exception {
        // scenario
        NewAuthorRequest request = new NewAuthorRequest(
                "a".repeat(121),
                "b".repeat(61),
                LocalDate.now().plusDays(1));

        // action (and validation)
        mockMvc.perform(post("/api/v1/authors")
                        .contentType(APPLICATION_JSON)
                        .content(toJson(request))
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "en"))
                .andExpect(status().isBadRequest())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, is(MediaTypes.PROBLEM_VALUE)))
                .andExpect(jsonPath("$.type", is("https://zalando.github.io/problem/constraint-violation")))
                .andExpect(jsonPath("$.title", is("Constraint Violation")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.violations", hasSize(4)))
                .andExpect(jsonPath("$.violations", containsInAnyOrder(
                                violation("name", "size must be between 0 and 120"),
                                violation("email", "size must be between 0 and 60"),
                                violation("email", "must be a well-formed email address"),
                                violation("birthdate", "must be a past date")
                        )
                ))
        ;

        // validation
        assertEquals(0, repository.count());
    }

    @Test
    @DisplayName("should not create a new author when he/she is underage")
    public void t4() throws Exception {
        // scenario
        NewAuthorRequest request = new NewAuthorRequest(
                "Jordi",
                "jordi.silva@zup.com.br",
                LocalDate.now().minusYears(17));

        // action (and validation)
        mockMvc.perform(post("/api/v1/authors")
                        .contentType(APPLICATION_JSON)
                        .content(toJson(request))
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "en"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, is(MediaTypes.PROBLEM_VALUE)))
                .andExpect(jsonPath("$.title", is("Unprocessable Entity")))
                .andExpect(jsonPath("$.status", is(422)))
                .andExpect(jsonPath("$.detail", is("author is underage")))
        ;

        // validation
        assertEquals(0, repository.count());
    }


    private Map<String, Object> violation(String field, String message) {
        return Map.of(
            "field", field,
            "message", message
        );
    }

    private String toJson(Object payload) throws JsonProcessingException {
        return mapper.writeValueAsString(payload);
    }

}