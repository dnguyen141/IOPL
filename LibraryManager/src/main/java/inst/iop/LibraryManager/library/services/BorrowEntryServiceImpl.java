package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.authentication.repositories.UserRepository;
import inst.iop.LibraryManager.library.dtos.CreateBorrowEntryDto;
import inst.iop.LibraryManager.library.dtos.UpdateBorrowEntryDto;
import inst.iop.LibraryManager.library.entities.Book;
import inst.iop.LibraryManager.library.entities.BorrowEntry;
import inst.iop.LibraryManager.library.entities.enums.BorrowStatus;
import inst.iop.LibraryManager.library.repositories.BookRepository;
import inst.iop.LibraryManager.library.repositories.BorrowEntryRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Data
@Service
public class BorrowEntryServiceImpl implements BorrowEntryService {
  private final BorrowEntryRepository borrowEntryRepository;
  private final BookRepository bookRepository;
  private final UserRepository userRepository;

  @Override
  public Optional<BorrowEntry> getBorrowEntryById(Long id) {
    return borrowEntryRepository.getBorrowEntryById(id);
  }

  @Override
  public List<BorrowEntry> listBorrowEntriesByStatus(BorrowStatus status) {
    return borrowEntryRepository.findBorrowEntriesByStatus(status);
  }

  @Override
  public List<BorrowEntry> listBorrowEntriesByUserIdAndStatus(User user, BorrowStatus status)
      throws NoSuchElementException {

    return borrowEntryRepository.findBorrowEntriesByUserIdAndStatus(user, status);
  }

  @Override
  public List<BorrowEntry> listBorrowEntriesByBookIdAndStatus(Book book, String status) {
    return borrowEntryRepository.findRequestedBorrowEntriesByBookIdAndStatus(book, BorrowStatus.valueOf(status));
  }

  @Override
  @Transactional
  public void createBorrowEntry(CreateBorrowEntryDto borrowEntryDto) {
    Optional<User> u = userRepository.findUserById(borrowEntryDto.getUserId());
    Optional<Book> b = bookRepository.findBookById(borrowEntryDto.getBookId());

    if (u.isEmpty() || b.isEmpty()) {
      return;
    }

    Book book = b.get();
    User user = u.get();

    List<BorrowEntry> borrowed = borrowEntryRepository.findOpenedBorrowEntries();

    if (book.getQuantity() >= borrowed.size()) {
      BorrowEntry borrowEntry = BorrowEntry.builder()
          .user(user)
          .book(book)
          .borrowDate(LocalDate.now())
          .returnDate(borrowEntryDto.getReturnDate())
          .status(BorrowStatus.Requested)
          .build();
      borrowEntryRepository.save(borrowEntry);
    }
  }

  @Override
  @Transactional
  public void updateBorrowEntryById(Long id, UpdateBorrowEntryDto borrowEntryDto) throws IllegalArgumentException {
    BorrowEntry borrowEntry = borrowEntryRepository.getBorrowEntryById(id)
        .orElseThrow(() -> new NoSuchElementException("Borrow entry with id " + id + " can't be found"));

    updateBook(borrowEntry, borrowEntryDto);

    updateBorrowEntryFields(borrowEntry, borrowEntryDto);

    borrowEntryRepository.save(borrowEntry);
  }

  private void updateBook(BorrowEntry borrowEntry, UpdateBorrowEntryDto borrowEntryDto) {
    Long newBookId = borrowEntryDto.getBookId();
    if (newBookId != null) {
      Book newBook = bookRepository.findBookById(newBookId).orElse(borrowEntry.getBook());
      borrowEntry.setBook(newBook);
      BorrowStatus oldStatus = borrowEntry.getStatus();
      BorrowStatus newStatus = BorrowStatus.getBorrowStatusFromString(borrowEntryDto.getBorrowStatus())
          .orElse(borrowEntry.getStatus());
      int available = newBook.getAvailable();
      if (!oldStatus.equals(newStatus)) {
        available = BorrowStatus.calculateNewAvailable(oldStatus, newStatus, available);
        if (available < 0) {
          throw new IllegalArgumentException("Invalid value for book: book is not available to borrow");
        }
        newBook.setAvailable(available);
        bookRepository.save(newBook);
      }
    }
  }

  private void updateBorrowEntryFields(BorrowEntry borrowEntry, UpdateBorrowEntryDto borrowEntryDto) {
    borrowEntry.setUser(userRepository.findUserById(borrowEntryDto.getUserId()).orElse(borrowEntry.getUser()));
    borrowEntry.setBorrowDate(borrowEntryDto.getBorrowDate());
    borrowEntry.setReturnDate(borrowEntryDto.getReturnDate());
    borrowEntry.setStatus(BorrowStatus.getBorrowStatusFromString(borrowEntryDto.getBorrowStatus())
        .orElse(borrowEntry.getStatus()));
  }

  @Override
  @Transactional
  public void deleteBorrowEntryById(Long id) throws IllegalArgumentException {
    BorrowEntry borrowEntry = borrowEntryRepository.getBorrowEntryById(id)
        .orElseThrow(() -> new NoSuchElementException("Borrow entry with id " + id + " can't be found"));
    Book book = borrowEntry.getBook();
    book.setAvailable(book.getAvailable() - borrowEntry.getStatus().getState());
    bookRepository.save(book);
    borrowEntryRepository.deleteBorrowEntryById(id);
  }
}
