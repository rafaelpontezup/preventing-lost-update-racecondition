package br.com.stackspot.nullbank.misc;

import org.hibernate.validator.constraints.ISBN;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static org.hibernate.validator.constraints.ISBN.Type.ISBN_13;

public record BookRequest(
    @NotBlank
    @ISBN(type = ISBN_13)
    String isbn,
    @NotBlank
    @Size(max = 120)
    String title,
    @NotBlank
    @Size(max = 4000)
    String description
){}
