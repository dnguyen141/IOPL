package inst.iop.LibraryManager.library.repositories;

import inst.iop.LibraryManager.library.entities.Book;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface BookRepository extends JpaRepository<Book, Long> {

  @Query("SELECT b from Book b")
  Page<Book> findAllBooks(Pageable pageable);

  @Query("SELECT b from Book b WHERE b.id = :id")
  Optional<Book> findBookById(Long id);

  @Query("SELECT b from Book b WHERE lower(b.title) = trim(lower(:title))")
  Optional<Book> findBookByTitle(String title);

  @Query("SELECT COUNT(b) FROM Book b")
  Integer getNumberOfBooks();

  @Query("SELECT Max(b.id) FROM Book b")
  Integer getHighestBookId();

  @Query(
      "SELECT b FROM Book b " +
      "WHERE ((:title is null or lower(b.title) like '%' || trim(lower(:title)) || '%') or " +
      "(:authors is null or lower(b.authors) like '%' || trim(lower(:authors)) || '%') or " +
      "(:publisher is null or lower(b.publisher) like '%' || trim(lower(:publisher)) || '%') or " +
      "(:type is null or lower(b.type.name) like trim(lower(:type)) || '%') or " +
      "(:field is null or lower(b.field.name) like trim(lower(:field)) || '%') or " +
      "(:isbn is null or b.isbn like trim(:isbn) || '%') or " +
      "(:inventoryNumber is null or lower(b.inventoryNumber) like trim(lower(:inventoryNumber)) || '%')) and " +
      "(:beforeYear is null or b.year >= :beforeYear) and " +
      "(:afterYear is null or b.year <= :afterYear)"
  )
  Page<Book> findBooks(String title, String authors, String publisher, String type, String field,
                       Integer beforeYear, Integer afterYear, String isbn, String inventoryNumber, Pageable pageable);

  @Query(
      "SELECT b FROM Book b " +
      "WHERE lower(b.title) like '%' || trim(lower(:title)) || '%' and " +
      "(:beforeYear is null or b.year >= :beforeYear) and " +
      "(:afterYear is null or b.year <= :afterYear)"
  )
  Page<Book> findBooksByTitle(String title, Integer beforeYear, Integer afterYear, Pageable pageable);

  @Query(
      "SELECT b FROM Book b " +
          "WHERE lower(b.authors) like '%' || trim(lower(:authors)) || '%' and " +
          "(:beforeYear is null or b.year >= :beforeYear) and " +
          "(:afterYear is null or b.year <= :afterYear)"
  )
  Page<Book> findBooksByAuthors(String authors, Integer beforeYear, Integer afterYear, Pageable pageable);

  @Query(
      "SELECT b FROM Book b WHERE lower(b.publisher) like '%' || trim(lower(:publisher)) || '%' and " +
          "(:beforeYear is null or b.year >= :beforeYear) and " +
          "(:afterYear is null or b.year <= :afterYear)"
  )
  Page<Book> findBooksByPublisher(String publisher, Integer beforeYear, Integer afterYear, Pageable pageable);

  @Query(
      "SELECT b FROM Book b WHERE lower(b.type.name) like trim(lower(:type)) || '%' and " +
          "(:beforeYear is null or b.year >= :beforeYear) and " +
          "(:afterYear is null or b.year <= :afterYear)"
  )
  Page<Book> findBooksByType(String type, Integer beforeYear, Integer afterYear, Pageable pageable);

  @Query(
      "SELECT b FROM Book b WHERE lower(b.field.name) like trim(lower(:field)) and " +
          "(:beforeYear is null or b.year >= :beforeYear) and " +
          "(:afterYear is null or b.year <= :afterYear)"
  )
  Page<Book> findBooksByField(String field, Integer beforeYear, Integer afterYear, Pageable pageable);

  @Query(
      "SELECT b FROM Book b WHERE b.isbn like trim(:isbn) || '%' and " +
          "(:beforeYear is null or b.year >= :beforeYear) and " +
          "(:afterYear is null or b.year <= :afterYear)"
  )
  Page<Book> findBooksByIsbn(String isbn, Integer beforeYear, Integer afterYear, Pageable pageable);

  @Query(
      "SELECT b FROM Book b WHERE lower(b.inventoryNumber) like trim(lower(:inventoryNumber)) || '%' and " +
          "(:beforeYear is null or b.year >= :beforeYear) and " +
          "(:afterYear is null or b.year <= :afterYear)"
  )
  Page<Book> findBooksByInventoryNumber(String inventoryNumber, Integer beforeYear,
                                        Integer afterYear, Pageable pageable);

  @Modifying
  @Transactional
  @Query("DELETE Book b WHERE b.id = :id")
  void deleteBookById(Long id);
}
