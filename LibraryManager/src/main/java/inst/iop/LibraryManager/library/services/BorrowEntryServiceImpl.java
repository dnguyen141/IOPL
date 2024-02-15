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
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static inst.iop.LibraryManager.utilities.ConstraintViolationSetHandler.convertConstrainViolationSetToMap;

@Data
@Service
public class BorrowEntryServiceImpl implements BorrowEntryService {
  private final BorrowEntryRepository borrowEntryRepository;
  private final BookRepository bookRepository;
  private final UserRepository userRepository;
  private final Validator validator;

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public BorrowEntry getBorrowEntryById(Long id) throws BadRequestDetailsException {
    return borrowEntryRepository.getBorrowEntryById(id).orElseThrow(
        () -> {
          Map<String, String> violations = new HashMap<>();
          violations.put("id", "There is no borrow entry with id " + id);
          return new BadRequestDetailsException("Invalid get borrow entry by id request", violations);
        }
    );
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public Page<BorrowEntry> listBorrowEntriesByStatus(String status, Integer pageNumber, Integer pageSize)
      throws BadRequestDetailsException {
    Map<String, String> violations = convertConstrainViolationSetToMap(validator.validate(status));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid list borrow entries by status request", violations);
    }

    BorrowStatus borrowStatus = BorrowStatus.getBorrowStatusFromString(status).orElseThrow(
        () -> {
          violations.put("status", "Invalid status for borrow entry");
          return new BadRequestDetailsException("Unable to list borrow entries", violations);
        }
    );
    return borrowEntryRepository.findBorrowEntriesByStatus(borrowStatus,
        PageRequest.of(pageNumber, pageSize));
  }

  @Override
  public Page<BorrowEntry> listBorrowEntriesByUsernameAndStatus(
      String username, String status, Integer pageNumber, Integer pageSize
  ) throws BadRequestDetailsException {
    Map<String, String> violations = convertConstrainViolationSetToMap(validator.validate(status));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid list borrow entries by user id and status request", violations);
    }

    User user = userRepository.findUserByEmail(username).orElseThrow(
        () -> {
          violations.put("userId", "There is no user with username " + username);
          return new BadRequestDetailsException("Invalid list borrow entries by status request", violations);
        }
    );

    BorrowStatus borrowStatus = BorrowStatus.getBorrowStatusFromString(status).orElseThrow(
        () -> {
          violations.put("status", "Invalid status for borrow entry");
          return new BadRequestDetailsException("Unable to list borrow entries", violations);
        }
    );

    return borrowEntryRepository.findBorrowEntriesByUserIdAndStatus(user, borrowStatus,
        PageRequest.of(pageNumber, pageSize));
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public Page<BorrowEntry> listBorrowEntriesByBookIdAndStatus(Long bookId, String status, Integer pageNumber,
                                                              Integer pageSize) throws BadRequestDetailsException {
    Map<String, String> violations = convertConstrainViolationSetToMap(validator.validate(status));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid list borrow entries by status request", violations);
    }

    Book book = bookRepository.getBookById(bookId).orElseThrow(
        () -> {
          violations.put("bookId", "There is no book with id " + bookId);
          return new BadRequestDetailsException("Invalid list borrow entries by status request", violations);
        }
    );

    BorrowStatus borrowStatus = BorrowStatus.getBorrowStatusFromString(status).orElseThrow(
        () -> {
          violations.put("status", "Invalid status for borrow entry");
          return new BadRequestDetailsException("Unable to list borrow entries", violations);
        }
    );

    return borrowEntryRepository.findBorrowEntriesByBookIdAndStatus(book, borrowStatus,
        PageRequest.of(pageNumber, pageSize));
  }

  @Override
  public Integer getBookAvailable(Long bookID) {
    Book book = bookRepository.getBookById(bookID).orElseThrow(
        () -> {
          Map<String, String> violations = new HashMap<>();
          violations.put("bookId", "There is no book with id " + bookID);
          return new BadRequestDetailsException("Unable to get book availability", violations);
        }
    );

    return getBookAvailability(book);
  }

  @Override
  @Transactional
  public void createBorrowEntry(CreateBorrowEntryDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertConstrainViolationSetToMap(validator.validate(request));

    User user = userRepository.findUserById(request.getUserId()).orElseThrow(
        () -> {
          violations.put("userId", "There is no user with username " + request.getUserId());
          return new BadRequestDetailsException("Unable to create new borrow entry", violations);
        }
    );

    Book book = bookRepository.getBookById(request.getBookId()).orElseThrow(
        () -> {
          violations.put("bookId", "There is no book with id " + request.getBookId());
          return new BadRequestDetailsException("Unable to create new borrow entry", violations);
        }
    );

    if (!checkIfReturnDateAfterIssuedDate(request.getReturnDate(), LocalDate.now())) {
      violations.put("returnDate", "Return date must be in the future");
      throw new BadRequestDetailsException("Unable to create new borrow entry", violations);
    }

    if (getBookAvailability(book) <= 0) {
      violations.put("bookId", "Book with id " + request.getBookId() + " is not available to borrow");
      throw new BadRequestDetailsException("Unable to create new borrow entry", violations);
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String callerRole = authentication.getAuthorities().stream().toList().get(0).toString();
    BorrowStatus borrowStatus = BorrowStatus.getBorrowStatusFromString(request.getStatus()).orElseThrow(
        () -> {
          violations.put("status", "Invalid borrow status");
          return new BadRequestDetailsException("Unable to create new borrow entry", violations);
        }
    );

    List<BorrowEntry> be = borrowEntryRepository.getRequestedBorrowEntryByUser(user, book);
    if (!be.isEmpty() && borrowStatus.equals(BorrowStatus.Requested)) {
      violations.put("status", "You already requested to borrow this book");
      throw  new BadRequestDetailsException("Unable to create new borrow entry", violations);
    }

    BorrowEntry borrowEntry = BorrowEntry.builder()
        .user(user)
        .book(book)
        .borrowDate(LocalDate.now())
        .returnDate(request.getReturnDate())
        .status(callerRole.equals("ROLE_USER") ? BorrowStatus.Requested : borrowStatus)
        .build();
    borrowEntryRepository.save(borrowEntry);

    bookRepository.save(book);
  }

  public int getBookAvailability(Book book) {
    return book.getQuantity() - borrowEntryRepository.countOpenedBorrowEntriesByBook(book);
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public void updateBorrowEntryById(Long id, UpdateBorrowEntryDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertConstrainViolationSetToMap(validator.validate(request));

    BorrowEntry borrowEntry = borrowEntryRepository.getBorrowEntryById(id).orElseThrow(
        () -> {
          violations.put("id", "Borrow entry with id " + id + " is not exists");
          return new BadRequestDetailsException("Unable to update borrow entry by id", violations);
        });

    if (request.getUserId() != null) {
      User newUser = userRepository.findUserById(request.getUserId()).orElseThrow(
          () -> {
            violations.put("userId", "There is no user with username " + request.getUserId());
            return new BadRequestDetailsException("Unable to create new borrow entry", violations);
          }
      );
      borrowEntry.setUser(newUser);
    }

    if (request.getBookId() != null) {
      Book newBook = bookRepository.getBookById(request.getBookId()).orElseThrow(
          () -> {
            violations.put("bookId", "There is no book with id " + request.getBookId());
            return new BadRequestDetailsException("Unable to create new borrow entry", violations);
          }
      );
      borrowEntry.setBook(newBook);
    }

    if (request.getBorrowDate() != null) {
      borrowEntry.setBorrowDate(request.getBorrowDate());
    }

    if (request.getReturnDate() != null) {
      borrowEntry.setReturnDate(request.getBorrowDate());
      if (checkIfReturnDateAfterIssuedDate(request.getReturnDate(), borrowEntry.getBorrowDate())) {
        violations.put("returnDate", "Return date must be in the future");
        throw new BadRequestDetailsException("Unable to create new borrow entry", violations);
      }
    }

    String status = request.getBorrowStatus();
    if (status != null) {
      Optional<BorrowStatus> borrowStatus = BorrowStatus.getBorrowStatusFromString(status);
      if (borrowStatus.isEmpty()) {
        violations.put("borrowStatus", "Invalid borrow entry status");
        throw new BadRequestDetailsException("Unable to update borrow entry", violations);
      }
      borrowEntry.setStatus(borrowStatus.get());
    }

    borrowEntryRepository.save(borrowEntry);
  }

  private boolean checkIfReturnDateAfterIssuedDate(LocalDate returnDate, LocalDate issuedDate) {
    return returnDate.isAfter(issuedDate);
  }

  @Override
  @Transactional
  public void deleteBorrowEntryById(Long id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String callerRole = authentication.getAuthorities().stream().toList().get(0).toString();

    BorrowEntry borrowEntry = borrowEntryRepository.getBorrowEntryById(id).orElseThrow(
        () -> {
          Map<String, String> violations = new HashMap<>();
          violations.put("id", "Borrow entry with id " + id + " is not exists");
          return new BadRequestDetailsException("Unable to delete borrow entry by id", violations);
        });

    if (callerRole.equals("ROLE_USER") && !borrowEntry.getStatus().equals(BorrowStatus.Requested)) {
      Map<String, String> violations = new HashMap<>();
      violations.put("id", "You are not allowed to delete this borrow entry");
      throw  new BadRequestDetailsException("Unable to delete borrow entry by id", violations);
    }

    borrowEntryRepository.deleteBorrowEntryById(id);
  }
}
