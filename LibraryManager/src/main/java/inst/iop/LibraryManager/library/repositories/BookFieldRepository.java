package inst.iop.LibraryManager.library.repositories;

import inst.iop.LibraryManager.library.entities.BookField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface BookFieldRepository extends JpaRepository<BookField, Long> {

  @Query("SELECT bf FROM BookField bf ORDER BY bf.id")
  List<BookField> getAllBookFields();

  @Query("SELECT bf FROM BookField bf WHERE bf.id = :id")
  Optional<BookField> getBookFieldById(Long id);

  @Query("SELECT bf FROM BookField bf WHERE lower(bf.name) = lower(:field)")
  Optional<BookField> getBookFieldByString(String field);

  @Modifying
  @Query("UPDATE BookField bf " +
      "SET bf.name = CASE WHEN (:name is not null) THEN :name ELSE bf.name END " +
      "WHERE bf.id = :id")
  void updateBookFieldById(Long id, String name);

  @Modifying
  @Query("DELETE FROM BookField bf WHERE bf.id = :id")
  void deleteBookFieldById(Long id);
}
