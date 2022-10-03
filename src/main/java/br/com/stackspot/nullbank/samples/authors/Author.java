package br.com.stackspot.nullbank.samples.authors;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.UUID;

public class Author {

    @NotNull
    private final UUID id;

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

    /**
     * Tip: Although optional, we can give some tips about the constraints of
     * constructor's arguments using Bean Validation's annotations.
     */
    public Author(@NotEmpty @Size(max = 120) String name,
                  @Email @NotEmpty @Size(max = 60) String email,
                  @Past @NotNull LocalDate birthdate) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.birthdate = birthdate;
        // TODO: asserts
    }

    public UUID getId() {
        return id;
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

    /**
     * Tip: Yeah, we CAN and SHOULD have business logic inside entities as well ;-)
     */
    public Integer getAge() {
        LocalDate today = LocalDate.now();
        int age = Period.between(this.birthdate, today).getYears();
        return age;
    }

    /**
     * Tip: Don't forget to think a little about the entity's identity, because every entity has an identity.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return email.equals(author.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", birthdate=" + birthdate +
                ", age=" + this.getAge() +
                '}';
    }
}
