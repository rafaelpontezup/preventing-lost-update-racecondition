package br.com.stackspot.nullbank.misc;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unfortunately there's no Bean Validation test-slice annotation
 * https://docs.spring.io/spring-boot/appendix/test-auto-configuration/slices.html
 */
@SpringBootTest(
    classes = ValidationAutoConfiguration.class // Configures the validation infrastructure
)
@ActiveProfiles("test")
class BookRequestTest {

    @Autowired
    private Validator validator;

    /**
     * Initializes the Bean Validation in standalone mode
     */
//    @BeforeEach
//    public void setUp() {
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        validator = factory.getValidator();
//    }

    @Test
    @DisplayName("must be a valid book")
    void t1() {
        // scenario
        BookRequest book = new BookRequest("9788550800653", "Domain-Driven Design", "DDD");

        // action
        Set<ConstraintViolation<BookRequest>> constraints = validator.validate(book);

        // validation
        assertEquals(0, constraints.size());
    }

    @Test
    @DisplayName("must NOT be a valid book")
    void t2() {
        // scenario
        BookRequest book = new BookRequest("97885-invalid", "a".repeat(121), "");

        // action
        Set<ConstraintViolation<BookRequest>> constraints = validator.validate(book);

        // validation
        assertConstraintErrors(constraints,
                tuple("isbn", "invalid ISBN"),
                tuple("title", "size must be between 0 and 120"),
                tuple("description", "must not be blank")
        );
    }

    /**
     * Asserts Bean Validation constraint errors
     */
    private <T> void assertConstraintErrors(Set<ConstraintViolation<T>> constraints, Tuple...tuples) {
        assertThat(constraints)
                .hasSize(tuples.length)
                .extracting(
                        t -> t.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                )
                .containsExactlyInAnyOrder(tuples)
        ;
    }

}