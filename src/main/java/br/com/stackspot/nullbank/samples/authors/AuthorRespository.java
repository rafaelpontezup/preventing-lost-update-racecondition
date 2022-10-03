package br.com.stackspot.nullbank.samples.authors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tip: Here we can use Spring Data JPA to make our lives easier.
 */
@Repository
public class AuthorRespository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorRespository.class);

    /**
     * Just a simple In-Memory Database
     */
    private final Map<UUID, Author> DATABASE = new ConcurrentHashMap<>();

    public Author save(Author author) {
        LOGGER.info(
            "Persisting a new author into database..."
        );
        DATABASE.put(author.getId(), author);
        return author;
    }

    public List<Author> findAll() {
        return new ArrayList<>(
                DATABASE.values().stream()
                        .sorted((o1, o2) -> o2.getAge() - o1.getAge())
                        .toList()
        );
    }

    public void deleteAll() {
        LOGGER.info(
            "Deleting all authors from database..."
        );
        DATABASE.clear();
    }

    public int count() {
        return DATABASE.size();
    }
}
