package inst.iop.LibraryManager.library.repositories;


import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.library.entities.Book;
import inst.iop.LibraryManager.library.entities.BorrowEntry;
import inst.iop.LibraryManager.library.entities.enums.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface BorrowEntryRepository extends JpaRepository<BorrowEntry, Long> {

  @Query("SELECT be FROM BorrowEntry be WHERE be.id = :id")
  Optional<BorrowEntry> getBorrowEntryById(Long id);

  @Query("SELECT be FROM BorrowEntry be WHERE be.status = :status")
  List<BorrowEntry> findBorrowEntriesByStatus(BorrowStatus status);

  @Query("SELECT be FROM BorrowEntry be " +
      "WHERE be.status = inst.iop.LibraryManager.library.entities.enums.BorrowStatus.Issued " +
      "or be.status = inst.iop.LibraryManager.library.entities.enums.BorrowStatus.Overdue")
  List<BorrowEntry> findOpenedBorrowEntries();

  @Query("SELECT be FROM BorrowEntry be WHERE be.user = :user AND be.status = :status")
  List<BorrowEntry> findBorrowEntriesByUserIdAndStatus(User user, BorrowStatus status);

  @Query("SELECT be FROM BorrowEntry be WHERE be.book = :book AND be.status = :status")
  List<BorrowEntry> findRequestedBorrowEntriesByBookIdAndStatus(Book book, BorrowStatus status);

  @Query("SELECT count(be) FROM BorrowEntry be WHERE be.book.id = :bookId AND be.status = :status")
  Integer countBorrowEntriesByBookIdAndStatus(Long bookId, BorrowStatus status);

  @Modifying
  @Query("DELETE BorrowEntry be WHERE be.id = :id")
  void deleteBorrowEntryById(Long id);
}
