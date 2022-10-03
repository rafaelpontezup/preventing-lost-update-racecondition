package br.com.stackspot.nullbank.samples.books;

import org.hibernate.validator.constraints.ISBN;

import javax.persistence.*;

import static org.hibernate.validator.constraints.ISBN.Type.ISBN_13;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_isbn", columnNames = "isbn")
        }
)
public class Book {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @ISBN(type = ISBN_13)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    public Book(String isbn, String title, String description) {
        this.isbn = isbn;
        this.title = title;
        this.description = description;
    }

    /**
     * @deprecated Exclusive use of Hibernate
     */
    @Deprecated
    public Book() {
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;

        Book book = (Book) o;

        if (id != null ? !id.equals(book.id) : book.id != null) return false;
        if (isbn != null ? !isbn.equals(book.isbn) : book.isbn != null) return false;
        if (title != null ? !title.equals(book.title) : book.title != null) return false;
        return description != null ? description.equals(book.description) : book.description == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (isbn != null ? isbn.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
