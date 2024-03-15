package inst.iop.LibraryManager.library.repositories;

import inst.iop.LibraryManager.authentication.entities.enums.Role;
import inst.iop.LibraryManager.library.entities.Book;
import inst.iop.LibraryManager.library.entities.BookField;
import inst.iop.LibraryManager.library.entities.BookType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface BookRepository extends JpaRepository<Book, Long> {

  @Query("SELECT b FROM Book b ORDER BY b.id")
  Page<Book> listAllBooks(Pageable pageable);

  @Query("SELECT b FROM Book b WHERE b.id = :id")
  Optional<Book> getBookById(Long id);

  @Query("SELECT b FROM Book b WHERE lower(b.title) = lower(:title)")
  Optional<Book> getBookByTitle(String title);

  Optional<Book> findFirstByOrderById();

  @Query("SELECT COUNT(b) FROM Book b")
  Integer getNumberOfBooks();

  @Query(
      "SELECT b FROM Book b " +
          "WHERE (lower(b.title) like concat('%', lower(:title), '%') or " +
          "lower(b.authors) like concat('%', lower(:authors), '%') or " +
          "lower(b.publisher) like concat('%', lower(:publisher), '%') or " +
          "lower(b.type.name) like concat('%', lower(:type), '%') or " +
          "lower(b.field.name) like concat('%', lower(:field), '%') or " +
          "b.isbn like concat('%', :isbn, '%') or " +
          "lower(b.inventoryNumber) like concat('%', lower(:inventoryNumber), '%')) and " +
          "(b.year <= :beforeYear and b.year >= :afterYear) " +
          "ORDER BY b.id"
  )
  Page<Book> findBooks(String title, String authors, String publisher, String type, String field,
                       Integer beforeYear, Integer afterYear, String isbn, String inventoryNumber, Pageable pageable);

  @Query(
      "SELECT b FROM Book b " +
          "WHERE lower(b.title) like concat('%', lower(:title), '%') and " +
          "(:beforeYear is null or b.year <= :beforeYear) and " +
          "(:afterYear is null or b.year >= :afterYear) " +
          "ORDER BY b.id"
  )
  Page<Book> findBooksByTitle(String title, Integer beforeYear, Integer afterYear, Pageable pageable);

  @Query(
      "SELECT b FROM Book b " +
          "WHERE lower(b.authors) like concat('%', lower(:authors), '%') and " +
          "(:beforeYear is null or b.year <= :beforeYear) and " +
          "(:afterYear is null or b.year >= :afterYear) " +
          "ORDER BY b.id"
  )
  Page<Book> findBooksByAuthors(String authors, Integer beforeYear, Integer afterYear, Pageable pageable);

  @Query(
      "SELECT b FROM Book b WHERE lower(b.publisher) like concat('%', lower(:publisher), '%') and " +
          "(:beforeYear is null or b.year <= :beforeYear) and " +
          "(:afterYear is null or b.year >= :afterYear) " +
          "ORDER BY b.id"
  )
  Page<Book> findBooksByPublisher(String publisher, Integer beforeYear, Integer afterYear, Pageable pageable);

  @Query(
      "SELECT b FROM Book b WHERE lower(b.type.name) like concat('%', lower(:type), '%') and " +
          "(:beforeYear is null or b.year <= :beforeYear) and " +
          "(:afterYear is null or b.year >= :afterYear) " +
          "ORDER BY b.id"
  )
  Page<Book> findBooksByType(String type, Integer beforeYear, Integer afterYear, Pageable pageable);

  @Query(
      "SELECT b FROM Book b WHERE lower(b.field.name) like concat('%', lower(:field), '%') and " +
          "(:beforeYear is null or b.year <= :beforeYear) and " +
          "(:afterYear is null or b.year >= :afterYear) " +
          "ORDER BY b.id"
  )
  Page<Book> findBooksByField(String field, Integer beforeYear, Integer afterYear, Pageable pageable);

  @Query(
      "SELECT b FROM Book b WHERE b.isbn like concat('%', :isbn, '%') and " +
          "(:beforeYear is null or b.year <= :beforeYear) and " +
          "(:afterYear is null or b.year >= :afterYear) " +
          "ORDER BY b.id"
  )
  Page<Book> findBooksByIsbn(String isbn, Integer beforeYear, Integer afterYear, Pageable pageable);

  @Query(
      "SELECT b FROM Book b WHERE lower(b.inventoryNumber) like concat('%', lower(:inventoryNumber), '%') and " +
          "(:beforeYear is null or b.year <= :beforeYear) and " +
          "(:afterYear is null or b.year >= :afterYear)"
  )
  Page<Book> findBooksByInventoryNumber(String inventoryNumber, Integer beforeYear,
                                        Integer afterYear, Pageable pageable);

  @Query("SELECT count(b) FROM Book b WHERE b.type = :type")
  Integer countBooksByType(BookType type);

  @Query("SELECT count(b) FROM Book b WHERE b.field = :field")
  Integer countBooksByField(BookField field);

  @Query(value = "SELECT currval('book_id_sequence')", nativeQuery = true)
  Long getCurrentBookIdSequenceValue();

  @Modifying
  @Query("UPDATE Book b " +
      "SET b.title = CASE WHEN (:title is not null) THEN :title ELSE b.title END, " +
      "b.authors = CASE WHEN (:authors is not null) THEN :authors ELSE b.authors END, " +
      "b.publisher = CASE WHEN (:publisher is not null) THEN :publisher ELSE b.publisher END, " +
      "b.type = CASE WHEN (:type is not null) THEN :type ELSE b.type END, " +
      "b.field = CASE WHEN (:field is not null) THEN :field ELSE b.field END, " +
      "b.year = CASE WHEN (:year is not null) THEN :year ELSE b.year END, " +
      "b.edition = CASE WHEN (:edition is not null) THEN :edition ELSE b.edition END, " +
      "b.isbn = CASE WHEN (:isbn is not null) THEN :isbn ELSE b.isbn END, " +
      "b.quantity = CASE WHEN (:quantity is not null) THEN :quantity ELSE b.quantity END, " +
      "b.inventoryNumber = CASE WHEN (:inventoryNumber is not null) THEN :inventoryNumber ELSE b.inventoryNumber END, " +
      "b.coverImage = CASE WHEN (:coverImage is not null) THEN :coverImage ELSE b.coverImage END " +
      "WHERE b.id = :id")
  void updateBookById(Long id, String title, String authors, String publisher, BookType type, BookField field,
                      Integer year, Integer edition, String isbn, String inventoryNumber, Integer quantity,
                      String coverImage);

  @Modifying
  @Transactional
  @Query("DELETE Book b WHERE b.id = :id")
  void deleteBookById(Long id);
}
