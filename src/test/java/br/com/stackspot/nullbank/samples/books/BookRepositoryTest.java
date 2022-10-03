package br.com.stackspot.nullbank.samples.books;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BookRepositoryTest {
    @Autowired
    private BookRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("must save a book")
    void t1() {

        Book book = new Book("978-8550800653", "Domain Drive Design", "DDD");

        repository.save(book);

        assertEquals(1, repository.findAll().size());

    }

    @Test
    @DisplayName("must get a book by isbn")
    void t2() {
        String isbn = "978-8550800653";
        Book book = new Book(isbn, "Domain Drive Design", "DDD");
        repository.save(book);

        Optional<Book> optionalBook = repository.findByIsbn(isbn);

        assertTrue(optionalBook.isPresent());
        assertEquals(book, optionalBook.get());
    }


    @Test
    @DisplayName("should not save a book with isbn already registered")
    void t3() {
        String isbn = "978-8550800653";
        Book ddd = new Book(isbn, "Domain Drive Design", "DDD");
        repository.save(ddd);

        assertThrows(DataIntegrityViolationException.class, () -> {

            Book cleanCode = new Book(isbn, "Clean Code", "clean code samples");
            repository.save(cleanCode);

        });

    }
}