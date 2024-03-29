package inst.iop.LibraryManager.library.repositories;


import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.library.entities.Book;
import inst.iop.LibraryManager.library.entities.BookField;
import inst.iop.LibraryManager.library.entities.BookType;
import inst.iop.LibraryManager.library.entities.BorrowEntry;
import inst.iop.LibraryManager.library.entities.enums.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface BorrowEntryRepository extends JpaRepository<BorrowEntry, Long> {

  @Query("SELECT be FROM BorrowEntry be WHERE be.id = :id")
  Optional<BorrowEntry> getBorrowEntryById(Long id);

  @Query("SELECT be FROM BorrowEntry be WHERE be.returnDate < current_date " +
      "AND be.status = inst.iop.LibraryManager.library.entities.enums.BorrowStatus.Issued")
  List<BorrowEntry> getLateBorrowEntry();

  @Query("SELECT be FROM BorrowEntry be WHERE be.user = :user and be.book = :book and " +
      "be.status = inst.iop.LibraryManager.library.entities.enums.BorrowStatus.Requested " +
      "ORDER BY be.id")
  List<BorrowEntry> listRequestedBorrowEntriesByUser(User user, Book book);

  @Query("SELECT be FROM BorrowEntry be WHERE be.status = :status ORDER BY be.id")
  Page<BorrowEntry> findBorrowEntriesByStatus(BorrowStatus status, Pageable pageable);

  @Query("SELECT count(be) FROM BorrowEntry be " +
      "WHERE (be.status = inst.iop.LibraryManager.library.entities.enums.BorrowStatus.Issued " +
      "or be.status = inst.iop.LibraryManager.library.entities.enums.BorrowStatus.Overdue " +
      "or be.status = inst.iop.LibraryManager.library.entities.enums.BorrowStatus.Lost) " +
      "and be.book = :book")
  Integer countOpenedBorrowEntriesByBook(Book book);

  @Query("SELECT count(be) FROM BorrowEntry be " +
      "WHERE (be.status = inst.iop.LibraryManager.library.entities.enums.BorrowStatus.Requested " +
      "or be.status = inst.iop.LibraryManager.library.entities.enums.BorrowStatus.Returned) " +
      "and be.book = :book")
  Integer countClosedBorrowEntries(Book book);

  @Query("SELECT be FROM BorrowEntry be WHERE be.user = :user AND be.status = :status ORDER BY be.id")
  Page<BorrowEntry> findBorrowEntriesByUserIdAndStatus(User user, BorrowStatus status, Pageable pageable);

  @Query("SELECT be FROM BorrowEntry be WHERE be.book = :book AND be.status = :status ORDER BY be.id")
  Page<BorrowEntry> findBorrowEntriesByBookIdAndStatus(Book book, BorrowStatus status, Pageable pageable);

  @Modifying
  @Query("UPDATE BorrowEntry be " +
      "SET be.user = CASE WHEN (:user is not null) THEN :user ELSE be.user END, " +
      "be.book = CASE WHEN (:book is not null) THEN :book ELSE be.book END, " +
      "be.borrowDate = CASE WHEN (:borrowDate is not null) THEN :borrowDate ELSE be.borrowDate END, " +
      "be.returnDate = CASE WHEN (:returnDate is not null) THEN :returnDate ELSE be.returnDate END, " +
      "be.status = CASE WHEN (:status is not null) THEN :status ELSE be.status END " +
      "WHERE be.id = :id")
  void updateBorrowEntryById(Long id, User user, Book book, LocalDate borrowDate, LocalDate returnDate,
                             BorrowStatus status);

  @Modifying
  @Query("DELETE BorrowEntry be WHERE be.id = :id")
  void deleteBorrowEntryById(Long id);
}
