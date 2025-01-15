package br.com.stackspot.nullbank.misc;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.spring.JqwikSpringSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Example of Property-based Testing with jQwik and Spring Boot
 */
@JqwikSpringSupport
@SpringBootTest(
    classes = ValidationAutoConfiguration.class // Configures ONLY the validation infrastructure
)
@ActiveProfiles("test")
class BookRequestPropertyBasedTest {

    @Autowired
    private Validator validator;

    @Property
    void validBookRequest(@ForAll("validIsbn13") String isbn,
                          @ForAll @AlphaChars @Whitespace @Chars({'.', ',', '!', '?'}) @NotBlank @StringLength(max = 120) String title,
                          @ForAll("validDescription") String description) {
        // scenario
        BookRequest book = new BookRequest(isbn, title, description);

        // action
        Set<ConstraintViolation<BookRequest>> constraints = validator.validate(book);

        // validation
        assertThat(constraints).isEmpty();
    }

    @Provide
    Arbitrary<String> validDescription() {
        return Arbitraries.strings()
                .alpha()
                .withChars(' ', '.', ',', '!', '?')
                .ofMinLength(1)
                .ofMaxLength(4000)
                .filter(text -> !text.isBlank());
    }

    @Provide
    Arbitrary<String> validIsbn13() {
        return Arbitraries.strings()
                .withChars('0', '9') // Generate only numeric characters
                .ofLength(12) // Generate the first 12 digits of the ISBN-13
                .map(Isbn13CheckDigitAppender::appendCheckDigit); // Add the check digit
    }

    /**
     * Generates an ISBN13 check digit and appends it to the informed ISBN
     */
    class Isbn13CheckDigitAppender {

        static String appendCheckDigit(String isbnWithoutCheckDigit) {
            int checkDigit = calculateIsbn13CheckDigit(isbnWithoutCheckDigit);
            return isbnWithoutCheckDigit + checkDigit;
        }

        private static int calculateIsbn13CheckDigit(String isbnWithoutCheckDigit) {
            int sum = 0;
            for (int i = 0; i < isbnWithoutCheckDigit.length(); i++) {
                int digit = Character.getNumericValue(isbnWithoutCheckDigit.charAt(i));
                sum += (i % 2 == 0) ? digit : digit * 3; // Alternate between multiplying by 1 and 3
            }
            int remainder = sum % 10;
            return (remainder == 0) ? 0 : 10 - remainder; // Calculate the check digit
        }
    }
}
