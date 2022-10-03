package br.com.stackspot.nullbank.samples.books;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
/*
    Since most repository calls are for read operations, it is good practice
    to define at the class level that transactions are read-only.
    reference: https://vladmihalcea.com/spring-transaction-best-practices/
 */
public interface BookRepository extends JpaRepository<Book,Long> {
    Optional<Book> findByIsbn(String isbn);
}
