package br.com.stackspot.nullbank.samples.authors;

import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.Period;

import static org.springframework.http.HttpStatus.*;

public class NewAuthorRequest {

    @NotEmpty
    @Size(max = 120)
    private final String name;

    @Email
    @NotEmpty
    @Size(max = 60)
    private final String email;

    @Past
    @NotNull
    private final LocalDate birthdate;

    public NewAuthorRequest(String name, String email, LocalDate birthdate) {
        this.name = name;
        this.email = email;
        this.birthdate = birthdate;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    @Override
    public String toString() {
        return "NewAuthorRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", birthdate=" + birthdate +
                '}';
    }

    /**
     * Verifies whether this author is underage or not
     */
    private boolean isUnderAge() {
        LocalDate today = LocalDate.now();
        int age = Period.between(this.birthdate, today).getYears();
        return age < 18;
    }

    /**
     * Converts this DTO to an entity or domain object
     */
    public Author toModel() {

        // Tip: Yeah, we CAN have business logic inside DTOs like this one ;-)
        if (isUnderAge()) {
            throw new ResponseStatusException(UNPROCESSABLE_ENTITY, "author is underage");
        }

        return new Author(name, email, birthdate);
    }

}
