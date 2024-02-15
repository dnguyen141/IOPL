package inst.iop.LibraryManager.library.repositories;

import inst.iop.LibraryManager.authentication.entities.enums.Role;
import inst.iop.LibraryManager.library.entities.BookType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface BookTypeRepository extends JpaRepository<BookType, Long> {

  @Query("SELECT bt FROM BookType bt ORDER BY bt.id")
  List<BookType> getAllBookType();

  @Query("SELECT bt FROM BookType bt WHERE bt.id = :id")
  Optional<BookType> getBookTypeById(Long id);

  @Query("SELECT bt FROM BookType bt WHERE lower(bt.name) = lower(:type)")
  Optional<BookType> getBookTypeByString(String type);

  @Modifying
  @Query("UPDATE BookType bt " +
      "SET bt.name = CASE WHEN (:name is not null) THEN :name ELSE bt.name END " +
      "WHERE bt.id = :id")
  void updateBookTypeById(Long id, String name);

  @Modifying
  @Query("DELETE FROM BookType bt WHERE bt.id = :id")
  void deleteBookTypeById(Long id);
}
