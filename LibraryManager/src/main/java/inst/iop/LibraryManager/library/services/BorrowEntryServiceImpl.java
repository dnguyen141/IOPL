package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.authentication.repositories.UserRepository;
import inst.iop.LibraryManager.library.dtos.CreateBorrowEntryDto;
import inst.iop.LibraryManager.library.dtos.ListBorrowEntriesByBookIdAndStatusDto;
import inst.iop.LibraryManager.library.dtos.ListBorrowEntriesByStatusDto;
import inst.iop.LibraryManager.library.dtos.UpdateBorrowEntryDto;
import inst.iop.LibraryManager.library.entities.Book;
import inst.iop.LibraryManager.library.entities.BorrowEntry;
import inst.iop.LibraryManager.library.entities.enums.BorrowStatus;
import inst.iop.LibraryManager.library.repositories.BookRepository;
import inst.iop.LibraryManager.library.repositories.BorrowEntryRepository;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static inst.iop.LibraryManager.utilities.ConstraintViolationSetHandler.convertSetToMap;

@RequiredArgsConstructor
@Service
public class BorrowEntryServiceImpl implements BorrowEntryService {
  private final BorrowEntryRepository borrowEntryRepository;
  private final BookRepository bookRepository;
  private final UserRepository userRepository;
  private final Validator validator;

  @Override
  public BorrowEntry getBorrowEntryById(Long id) throws BadRequestDetailsException {
    return borrowEntryRepository.getBorrowEntryById(id).orElseThrow(() -> {
      Map<String, String> violations = new HashMap<>();
      violations.put("id", "There is no borrow entry with id " + id);
      return new BadRequestDetailsException("Invalid get borrow entry by id request", violations);
    });
  }

  @Override
  public Page<BorrowEntry> listBorrowEntriesByStatus(ListBorrowEntriesByStatusDto request)
      throws BadRequestDetailsException {
    Map<String, String> violations = convertSetToMap(validator.validate(request));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Unable to list borrow entries by status", violations);
    }

    BorrowStatus borrowStatus = BorrowStatus.getBorrowStatusFromString(request.getStatus()).orElse(null);
    return borrowEntryRepository.findBorrowEntriesByStatus(borrowStatus,
        PageRequest.of(request.getPageNumber(), request.getPageSize()));
  }

  @Override
  public Page<BorrowEntry> listBorrowEntriesByUsernameAndStatus(
      String username, ListBorrowEntriesByStatusDto request
  ) throws BadRequestDetailsException {
    Map<String, String> violations = convertSetToMap(validator.validate(request));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Unable to list borrow entries by user and status", violations);
    }

    User user = userRepository.findUserByEmail(username).orElseThrow(() -> {
      violations.put("username", "There is no user with username " + username);
      return new BadRequestDetailsException("Unable to list borrow entries by user and status", violations);
    });

    BorrowStatus borrowStatus = BorrowStatus.getBorrowStatusFromString(request.getStatus()).orElse(null);

    return borrowEntryRepository.findBorrowEntriesByUserIdAndStatus(user, borrowStatus,
        PageRequest.of(request.getPageNumber(), request.getPageSize()));
  }

  @Override
  public Page<BorrowEntry> listBorrowEntriesByBookIdAndStatus(ListBorrowEntriesByBookIdAndStatusDto request)
      throws BadRequestDetailsException {
    Map<String, String> violations = convertSetToMap(validator.validate(request));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Unable to list borrow entries by book id and status", violations);
    }

    Book book = bookRepository.getBookById(request.getBookId()).orElseThrow(() -> {
      violations.put("bookId", "There is no book with id " + request.getBookId());
      return new BadRequestDetailsException("Unable to list borrow entries by book id and status", violations);
    });

    BorrowStatus borrowStatus = BorrowStatus.getBorrowStatusFromString(request.getStatus())
        .orElse(BorrowStatus.Requested);

    return borrowEntryRepository.findBorrowEntriesByBookIdAndStatus(book, borrowStatus,
        PageRequest.of(request.getPageNumber(), request.getPageSize()));
  }

  @Override
  public Integer getBookAvailability(Long bookID) {
    Book book = bookRepository.getBookById(bookID).orElseThrow(() -> {
      Map<String, String> violations = new HashMap<>();
      violations.put("bookId", "There is no book with id " + bookID);
      return new BadRequestDetailsException("Unable to get book availability", violations);
    });

    return getBookAvailability(book);
  }

  @Override
  @Transactional
  public BorrowEntry createBorrowEntry(CreateBorrowEntryDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertSetToMap(validator.validate(request));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Unable to create new borrow entry", violations);
    }

    if (!request.getReturnDate().isAfter(request.getBorrowDate())) {
      violations.put("returnDate", "Return date must be after borrow date");
      throw new BadRequestDetailsException("Unable to create new borrow entry", violations);
    }

    User user = userRepository.findUserById(request.getUserId()).orElseThrow(() -> {
      violations.put("userId", "There is no user with user id " + request.getUserId());
      return new BadRequestDetailsException("Unable to create new borrow entry", violations);
    });

    Book book = bookRepository.getBookById(request.getBookId()).orElseThrow(() -> {
      violations.put("bookId", "There is no book with id " + request.getBookId());
      return new BadRequestDetailsException("Unable to create new borrow entry", violations);
    });

    var authentication = SecurityContextHolder.getContext().getAuthentication();
    String callerRole = authentication.getAuthorities().stream().toList().get(0).toString();
    BorrowStatus borrowStatus = BorrowStatus.getBorrowStatusFromString(request.getStatus())
        .orElse(BorrowStatus.Requested);

    // if user gives out borrow request after noon, borrow date must be later than current date
    var currentDate = LocalDate.now();
    var deadline = currentDate.atTime(LocalTime.NOON);
    if (callerRole.equals("ROLE_USER") && !deadline.isAfter(LocalDateTime.now())
        && !request.getBorrowDate().isAfter(currentDate)) {
      violations.put("borrowDate", "Borrow date must be in the future");
      throw new BadRequestDetailsException("Unable to create new borrow entry", violations);
    }

    if (getBookAvailability(book) <= 0) {
      violations.put("bookId", "Book with id " + request.getBookId() + " is not available to borrow");
      throw new BadRequestDetailsException("Unable to create new borrow entry", violations);
    }

    List<BorrowEntry> be = borrowEntryRepository.listRequestedBorrowEntriesByUser(user, book);
    if (!be.isEmpty() && borrowStatus.equals(BorrowStatus.Requested)) {
      violations.put("status", "You already requested to borrow this book");
      throw new BadRequestDetailsException("Unable to create new borrow entry", violations);
    }

    BorrowEntry borrowEntry = BorrowEntry.builder()
        .user(user)
        .book(book)
        .borrowDate(request.getBorrowDate())
        .returnDate(request.getReturnDate())
        .status(callerRole.equals("ROLE_USER") ? BorrowStatus.Requested : borrowStatus)
        .build();
    return borrowEntryRepository.save(borrowEntry);
  }

  public int getBookAvailability(Book book) {
    return book.getQuantity() - borrowEntryRepository.countOpenedBorrowEntriesByBook(book);
  }

  @Override
  @Transactional
  public void updateBorrowEntryById(UpdateBorrowEntryDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertSetToMap(validator.validate(request));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Unable to update borrow entry by id", violations);
    }

    BorrowEntry borrowEntry = borrowEntryRepository.getBorrowEntryById(request.getBorrowEntryId()).orElseThrow(() -> {
      violations.put("borrowEntryId", "Borrow entry with id " + request.getBorrowEntryId() + " is not exists");
      return new BadRequestDetailsException("Unable to update borrow entry by id", violations);
    });

    User newUser = userRepository.findUserById(request.getUserId()).orElseThrow(() -> {
      violations.put("userId", "There is no user with username " + request.getUserId());
      return new BadRequestDetailsException("Unable to update borrow entry by id", violations);
    });

    Book newBook = bookRepository.getBookById(request.getBookId()).orElseThrow(() -> {
      violations.put("bookId", "There is no book with id " + request.getBookId());
      return new BadRequestDetailsException("Unable to update borrow entry by id", violations);
    });

    if (!request.getReturnDate().isAfter(borrowEntry.getBorrowDate())) {
      violations.put("returnDate", "Return date must be in the future");
      throw new BadRequestDetailsException("Unable to update borrow entry by id", violations);
    }

    BorrowStatus borrowStatus = BorrowStatus.getBorrowStatusFromString(request.getStatus())
        .orElse(BorrowStatus.Requested);

    borrowEntryRepository.updateBorrowEntryById(request.getBorrowEntryId(), newUser, newBook, request.getBorrowDate(),
        request.getReturnDate(), borrowStatus);
  }

  @Override
  @Transactional
  public void deleteBorrowEntryById(Long id) {
    borrowEntryRepository.deleteBorrowEntryById(id);
  }
}
