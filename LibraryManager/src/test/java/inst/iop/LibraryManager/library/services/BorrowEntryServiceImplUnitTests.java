package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.authentication.entities.enums.Role;
import inst.iop.LibraryManager.authentication.repositories.UserRepository;
import inst.iop.LibraryManager.library.dtos.CreateBorrowEntryDto;
import inst.iop.LibraryManager.library.dtos.ListBorrowEntriesByBookIdAndStatusDto;
import inst.iop.LibraryManager.library.dtos.ListBorrowEntriesByStatusDto;
import inst.iop.LibraryManager.library.dtos.UpdateBorrowEntryDto;
import inst.iop.LibraryManager.library.entities.Book;
import inst.iop.LibraryManager.library.entities.BookField;
import inst.iop.LibraryManager.library.entities.BookType;
import inst.iop.LibraryManager.library.entities.BorrowEntry;
import inst.iop.LibraryManager.library.entities.enums.BorrowStatus;
import inst.iop.LibraryManager.library.repositories.BookRepository;
import inst.iop.LibraryManager.library.repositories.BorrowEntryRepository;
import inst.iop.LibraryManager.library.services.BorrowEntryServiceImpl;
import inst.iop.LibraryManager.utilities.ConstraintViolationSetHandler;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import io.jsonwebtoken.lang.Collections;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
public class BorrowEntryServiceImplUnitTests {

  @Mock
  private BorrowEntryRepository borrowEntryRepository;

  @Mock
  private BookRepository bookRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private Validator validator;

  @InjectMocks
  private BorrowEntryServiceImpl borrowEntryService;

  @Mock
  private SecurityContextImpl securityContext;

  @Mock
  private UsernamePasswordAuthenticationToken authentication;

  private static final int mockPageNumber = 0;

  private static final int mockPageSize = 20;

  private User mockUser;

  private Book mockBook;

  private BorrowEntry mockBorrowEntry;

  private static final LocalDate mockLocalDateNow = LocalDate.of(2024, 1, 1);

  private static final LocalDateTime mockLocalDateTimeNow = LocalDateTime.of(mockLocalDateNow, LocalTime.NOON);

  @BeforeEach
  public void setUp() {
    mockUser = User.builder()
        .id(0L)
        .email("user@email.com")
        .password("testEncodedPassword")
        .firstName("User")
        .lastName("Name")
        .role(Role.USER)
        .createdDate(mockLocalDateNow)
        .enabled(false)
        .build();

    mockBook = Book.builder()
        .id(100L)
        .title("Book Title")
        .authors("Author")
        .publisher("Publisher")
        .type(BookType.builder().name("Book").build())
        .field(BookField.builder().name("Field").build())
        .year(2023)
        .edition(1)
        .isbn("123-4-567890-12-3")
        .inventoryNumber("100 CSST BO 2023")
        .coverImage("./src/main/resources/covers/100.jpg")
        .quantity(1)
        .build();

    mockBorrowEntry = BorrowEntry.builder()
        .id(0L)
        .user(mockUser)
        .book(mockBook)
        .borrowDate(mockLocalDateNow)
        .returnDate(mockLocalDateNow.plusDays(1))
        .status(BorrowStatus.Issued)
        .build();
  }

  @Test
  public void testGetBorrowEntryByIdHappyFlow() {
    log.info("Test running - Get borrow entry by id happy flow...");

    when(borrowEntryRepository.getBorrowEntryById(mockBorrowEntry.getId())).thenReturn(Optional.of(mockBorrowEntry));

    assertEquals(mockBorrowEntry, borrowEntryService.getBorrowEntryById(mockBorrowEntry.getId()));

    verify(borrowEntryRepository, times(1)).getBorrowEntryById(mockBorrowEntry.getId());

    log.info("Passed - Get borrow entry by id happy flow");
  }

  @Test
  public void testGetBorrowEntryByIdNotFoundFlow() {
    log.info("Test running - Get borrow entry by id when borrow entry can't be found...");

    when(borrowEntryRepository.getBorrowEntryById(mockBorrowEntry.getId())).thenReturn(Optional.empty());

    var exception = assertThrows(BadRequestDetailsException.class,
        () -> borrowEntryService.getBorrowEntryById(mockBorrowEntry.getId()));
    assertEquals(exception.getMessage(), "Invalid get borrow entry by id request");
    Map<String, String> violations = new HashMap<>();
    violations.put("id", "There is no borrow entry with id 0");
    assertEquals(exception.getViolations(), violations);

    verify(borrowEntryRepository, times(1)).getBorrowEntryById(mockBorrowEntry.getId());

    log.info("Passed - Get borrow entry by id when borrow entry can't be found");
  }

  @Test
  public void testListBorrowEntriesByStatusHappyFlow() {
    log.info("Test running - List borrow entries by status happy flow...");

    var expected = new PageImpl<>(List.of(mockBorrowEntry));
    var request = ListBorrowEntriesByStatusDto.builder()
        .status("Requested")
        .pageNumber(mockPageNumber)
        .pageSize(mockPageSize)
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(borrowEntryRepository.findBorrowEntriesByStatus(BorrowStatus.Requested,
          PageRequest.of(mockPageNumber, mockPageSize))).thenReturn(expected);

      assertEquals(expected, borrowEntryService.listBorrowEntriesByStatus(request));

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(borrowEntryRepository, times(1)).findBorrowEntriesByStatus(BorrowStatus.Requested,
          PageRequest.of(mockPageNumber, mockPageSize));
    }

    log.info("Passed - List borrow entries by status happy flow");
  }

  @Test
  public void testListBorrowEntriesByStatusInvalidInputsFlow() {
    log.info("Test running - List borrow entries by status when inputs are invalid...");

    var request = ListBorrowEntriesByStatusDto.builder()
        .status("Invalid status")
        .pageNumber(-1)
        .pageSize(0)
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      Map<String, String> violations = new HashMap<>();
      violations.put("status", "Borrow status must either be Requested, Issued, Returned, Overdue or Lost");
      violations.put("pageNumber", "Page number must be an integer that is bigger than 0");
      violations.put("pageSize", "Page size must be an integer of at least 1");
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(violations);

      var exception = assertThrows(BadRequestDetailsException.class,
          () -> borrowEntryService.listBorrowEntriesByStatus(request));
      assertEquals("Unable to list borrow entries by status", exception.getMessage());
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(borrowEntryRepository, never()).findBorrowEntriesByStatus(any(), any());
    }

    log.info("Passed - List borrow entries by status when inputs are invalid");
  }

  @Test
  public void testListBorrowEntriesByUsernameAndStatusHappyFlow() {
    log.info("Test running - List borrow entries by username and status happy flow...");

    var expected = new PageImpl<>(List.of(mockBorrowEntry));
    var request = ListBorrowEntriesByStatusDto.builder()
        .status("Requested")
        .pageNumber(mockPageNumber)
        .pageSize(mockPageSize)
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(userRepository.findUserByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
      when(borrowEntryRepository.findBorrowEntriesByUserIdAndStatus(mockUser, BorrowStatus.Requested,
          PageRequest.of(mockPageNumber, mockPageSize))).thenReturn(expected);

      assertEquals(expected, borrowEntryService.listBorrowEntriesByUsernameAndStatus(mockUser.getEmail(), request));

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(userRepository, times(1)).findUserByEmail(mockUser.getEmail());
      verify(borrowEntryRepository, times(1)).findBorrowEntriesByUserIdAndStatus(mockUser,
          BorrowStatus.Requested, PageRequest.of(mockPageNumber, mockPageSize));
    }

    log.info("Passed - List borrow entries by username and status happy flow");
  }

  @Test
  public void testListBorrowEntriesByUsernameAndStatusInvalidInputsFlow() {
    log.info("Test running - List borrow entries by username and status happy flow...");

    var request = ListBorrowEntriesByStatusDto.builder()
        .status("Invalid input")
        .pageNumber(-1)
        .pageSize(0)
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      Map<String, String> violations = new HashMap<>();
      violations.put("status", "Borrow status must either be Requested, Issued, Returned, Overdue or Lost");
      violations.put("pageNumber", "Page number must be an integer that is bigger than 0");
      violations.put("pageSize", "Page size must be an integer of at least 1");
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(violations);

      var exception = assertThrows(BadRequestDetailsException.class,
          () -> borrowEntryService.listBorrowEntriesByUsernameAndStatus(mockUser.getEmail(), request));
      assertEquals("Unable to list borrow entries by user and status", exception.getMessage());
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(userRepository, never()).findUserByEmail(mockUser.getEmail());
      verify(borrowEntryRepository, never()).findBorrowEntriesByUserIdAndStatus(any(), any(), any());
    }

    log.info("Passed - List borrow entries by username and status happy flow");
  }

  @Test
  public void testListBorrowEntriesByUsernameAndStatusUserNotFoundFlow() {
    log.info("Test running - List borrow entries by username and status when user can't be found flow...");

    var request = ListBorrowEntriesByStatusDto.builder()
        .status("Requested")
        .pageNumber(mockPageNumber)
        .pageSize(mockPageSize)
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(userRepository.findUserByEmail(mockUser.getEmail())).thenReturn(Optional.empty());

      var exception = assertThrows(BadRequestDetailsException.class,
          () -> borrowEntryService.listBorrowEntriesByUsernameAndStatus(mockUser.getEmail(), request));
      assertEquals("Unable to list borrow entries by user and status", exception.getMessage());
      Map<String, String> violations = new HashMap<>();
      violations.put("username", "There is no user with username " + mockUser.getEmail());
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(userRepository, times(1)).findUserByEmail(mockUser.getEmail());
      verify(borrowEntryRepository, never()).findBorrowEntriesByUserIdAndStatus(any(), any(), any());
    }

    log.info("Passed - List borrow entries by username and status when user can't be found flow");
  }

  @Test
  public void testListBorrowEntriesByBookIdAndStatusHappyFlow() {
    log.info("Test running - List borrow entries by book id and status happy flow...");

    var expected = new PageImpl<>(List.of(mockBorrowEntry));
    var request = ListBorrowEntriesByBookIdAndStatusDto.builder()
        .bookId(100L)
        .status("Requested")
        .pageNumber(mockPageNumber)
        .pageSize(mockPageSize)
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(bookRepository.getBookById(mockBook.getId())).thenReturn(Optional.of(mockBook));
      when(borrowEntryRepository.findBorrowEntriesByBookIdAndStatus(mockBook, BorrowStatus.Requested,
          PageRequest.of(mockPageNumber, mockPageSize))).thenReturn(expected);

      assertEquals(expected, borrowEntryService.listBorrowEntriesByBookIdAndStatus(request));

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, times(1)).getBookById(mockBook.getId());
      verify(borrowEntryRepository, times(1)).findBorrowEntriesByBookIdAndStatus(mockBook,
          BorrowStatus.Requested, PageRequest.of(mockPageNumber, mockPageSize));
    }

    log.info("Passed - List borrow entries by book id and status happy flow");
  }

  @Test
  public void testListBorrowEntriesByBookIdAndStatusInvalidInputsFlow() {
    log.info("Test running - List borrow entries by book id and status when inputs are invalid...");

    var request = ListBorrowEntriesByBookIdAndStatusDto.builder()
        .bookId(100L)
        .status("Invalid input")
        .pageNumber(-1)
        .pageSize(0)
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      Map<String, String> violations = new HashMap<>();
      violations.put("status", "Borrow status must either be Requested, Issued, Returned, Overdue or Lost");
      violations.put("pageNumber", "Page number must be an integer that is bigger than 0");
      violations.put("pageSize", "Page size must be an integer of at least 1");
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(violations);

      var exception = assertThrows(BadRequestDetailsException.class,
          () -> borrowEntryService.listBorrowEntriesByBookIdAndStatus(request));
      assertEquals("Unable to list borrow entries by book id and status" , exception.getMessage());
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, never()).getBookById(mockBook.getId());
      verify(borrowEntryRepository, never()).findBorrowEntriesByBookIdAndStatus(any(), any(), any());
    }

    log.info("Passed - List borrow entries by book id and status when inputs are invalid");
  }

  @Test
  public void testListBorrowEntriesByBookIdAndStatusBookNotFoundFlow() {
    log.info("Test running - List borrow entries by book id and status when book can't be found...");

    var request = ListBorrowEntriesByBookIdAndStatusDto.builder()
        .bookId(101L)
        .status("Requested")
        .pageNumber(mockPageNumber)
        .pageSize(mockPageSize)
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(bookRepository.getBookById(101L)).thenReturn(Optional.empty());

      var exception = assertThrows(BadRequestDetailsException.class,
          () -> borrowEntryService.listBorrowEntriesByBookIdAndStatus(request));
      assertEquals("Unable to list borrow entries by book id and status" , exception.getMessage());
      Map<String, String> violations = new HashMap<>();
      violations.put("bookId", "There is no book with id " + request.getBookId());
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, times(1)).getBookById(101L);
      verify(borrowEntryRepository, never()).findBorrowEntriesByBookIdAndStatus(any(), any(), any());
    }

    log.info("Passed - List borrow entries by book id and status when book can't be found");
  }

  @Test
  public void testGetBookAvailabilityHappyFlow() {
    log.info("Test running - Get book's availability happy flow...");

    when(bookRepository.getBookById(mockBook.getId())).thenReturn(Optional.of(mockBook));

    assertEquals(1, borrowEntryService.getBookAvailability(mockBook.getId()));

    verify(bookRepository, times(1)).getBookById(mockBook.getId());

    log.info("Passed - Get book's availability happy flow");
  }

  @Test
  public void testGetBookAvailabilityBookNotFoundFlow() {
    log.info("Test running - Get book's availability when book can't be found...");

    when(bookRepository.getBookById(101L)).thenReturn(Optional.empty());

    var exception = assertThrows(BadRequestDetailsException.class,
        () -> borrowEntryService.getBookAvailability(101L));
    assertEquals("Unable to get book availability", exception.getMessage());
    Map<String, String> violations = new HashMap<>();
    violations.put("bookId", "There is no book with id 101");
    assertEquals(violations, exception.getViolations());

    verify(bookRepository, times(1)).getBookById(any());

    log.info("Passed - Get book's availability when book can't be found");
  }

  @Test
  public void testCreateBorrowEntryHappyFlow() {
    log.info("Test running - Create borrow entry happy flow...");

    try (
        MockedStatic<LocalDate> ld = mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS);
        MockedStatic<LocalDateTime> ldt = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class);
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      ld.when(LocalDate::now).thenReturn(mockLocalDateNow);
      ldt.when(LocalDateTime::now).thenReturn(mockLocalDateTimeNow);

      User mockUser2 = User.builder()
          .id(1L)
          .email("anotherUser@email.com")
          .password("testEncodedPassword")
          .firstName("Another")
          .lastName("Name")
          .role(Role.USER)
          .createdDate(LocalDate.now())
          .enabled(false)
          .build();

      Book mockThesis = Book.builder()
          .id(101L)
          .title("Another Book Title")
          .authors("Author")
          .publisher("Publisher")
          .type(BookType.builder().name("Thesis").build())
          .field(BookField.builder().name("Field").build())
          .year(2023)
          .edition(1)
          .isbn("123-4-567890-12-4")
          .inventoryNumber("101 CSST TH 2023")
          .coverImage("./src/main/resources/covers/101.jpg")
          .quantity(1)
          .build();

      var request = CreateBorrowEntryDto.builder()
          .userId(mockUser2.getId())
          .bookId(mockThesis.getId())
          .status("Issued")
          .borrowDate(LocalDate.now().plusDays(1))
          .returnDate(LocalDate.now().plusDays(2))
          .build();

      var expected = BorrowEntry.builder()
          .user(mockUser2)
          .book(mockThesis)
          .status(BorrowStatus.Requested)
          .borrowDate(LocalDate.now().plusDays(1))
          .returnDate(LocalDate.now().plusDays(2))
          .build();

      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(userRepository.findUserById(mockUser2.getId())).thenReturn(Optional.of(mockUser2));
      when(bookRepository.getBookById(mockThesis.getId())).thenReturn(Optional.of(mockThesis));
      sch.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getAuthorities()).thenReturn(Collections.of(new SimpleGrantedAuthority("ROLE_USER")));
      when(borrowEntryRepository.countOpenedBorrowEntriesByBook(mockThesis)).thenReturn(0);
      when(borrowEntryRepository.listRequestedBorrowEntriesByUser(mockUser2, mockThesis)).thenReturn(List.of());
      when(borrowEntryRepository.save(expected)).thenReturn(expected);

      assertEquals(expected, borrowEntryService.createBorrowEntry(request));

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(userRepository, times(1)).findUserById(mockUser2.getId());
      verify(bookRepository, times(1)).getBookById(mockThesis.getId());
      sch.verify(SecurityContextHolder::getContext, times(1));
      verify(securityContext, times(1)).getAuthentication();
      verify(authentication, times(1)).getAuthorities();
      verify(borrowEntryRepository, times(1)).countOpenedBorrowEntriesByBook(mockThesis);
      verify(borrowEntryRepository, times(1)).listRequestedBorrowEntriesByUser(
          mockUser2, mockThesis);
      verify(borrowEntryRepository, times(1)).save(expected);
    }

    log.info("Passed - Create borrow entry happy flow");
  }

  @Test
  public void testCreateBorrowEntryInvalidInputsFlow() {
    log.info("Test running - Create borrow entry when inputs are invalid...");

    try (
        MockedStatic<LocalDate> ld = mockStatic(LocalDate.class);
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class);
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      ld.when(LocalDate::now).thenReturn(mockLocalDateNow);

      var request = CreateBorrowEntryDto.builder()
          .userId(mockUser.getId())
          .bookId(mockBook.getId())
          .status("Invalid input")
          .borrowDate(LocalDate.now())
          .returnDate(LocalDate.now())
          .build();

      Map<String, String> violations = new HashMap<>();
      violations.put("status", "Borrow status must either be Requested, Issued, Returned, Overdue or Lost");
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(violations);

      var exception = assertThrows(BadRequestDetailsException.class,
          () -> borrowEntryService.createBorrowEntry(request));
      assertEquals("Unable to create new borrow entry", exception.getMessage());
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(userRepository, never()).findUserById(any());
      verify(bookRepository, never()).getBookById(any());
      sch.verify(SecurityContextHolder::getContext, never());
      verify(securityContext, never()).getAuthentication();
      verify(authentication, never()).getAuthorities();
      verify(borrowEntryRepository, never()).listRequestedBorrowEntriesByUser(any(), any());
      verify(borrowEntryRepository, never()).save(any());
    }

    log.info("Passed - Create borrow entry when inputs are invalid");
  }

  @Test
  public void testCreateBorrowEntryInvalidReturnDateFlow() {
    log.info("Test running - Create borrow entry when return date is not after borrow date...");

    try (
        MockedStatic<LocalDate> ld = mockStatic(LocalDate.class);
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class);
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      ld.when(LocalDate::now).thenReturn(mockLocalDateNow);

      var request = CreateBorrowEntryDto.builder()
          .userId(mockUser.getId())
          .bookId(mockBook.getId())
          .status("Issued")
          .borrowDate(LocalDate.now())
          .returnDate(LocalDate.now())
          .build();

      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request))).thenReturn(new HashMap<>());

      var exception = assertThrows(BadRequestDetailsException.class,
          () -> borrowEntryService.createBorrowEntry(request));
      assertEquals("Unable to create new borrow entry", exception.getMessage());
      Map<String, String> violations = new HashMap<>();
      violations.put("returnDate", "Return date must be after borrow date");
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(userRepository, never()).findUserById(1L);
      verify(bookRepository, never()).getBookById(any());
      sch.verify(SecurityContextHolder::getContext, never());
      verify(securityContext, never()).getAuthentication();
      verify(authentication, never()).getAuthorities();
      verify(borrowEntryRepository, never()).countOpenedBorrowEntriesByBook(any());
      verify(borrowEntryRepository, never()).listRequestedBorrowEntriesByUser(any(), any());
      verify(borrowEntryRepository, never()).save(any());
    }

    log.info("Passed - Create borrow entry when return date is not after borrow date");
  }

  @Test
  public void testCreateBorrowEntryUserNotFoundFlow() {
    log.info("Test running - Create borrow entry when user can't be found...");

    try (
        MockedStatic<LocalDate> ld = mockStatic(LocalDate.class);
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class);
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      ld.when(LocalDate::now).thenReturn(mockLocalDateNow);

      var request = CreateBorrowEntryDto.builder()
          .userId(1L)
          .bookId(mockBook.getId())
          .status("Issued")
          .borrowDate(LocalDate.now().plusDays(1))
          .returnDate(LocalDate.now().plusDays(2))
          .build();

      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(userRepository.findUserById(1L)).thenReturn(Optional.empty());

      var exception = assertThrows(BadRequestDetailsException.class,
          () -> borrowEntryService.createBorrowEntry(request));
      assertEquals("Unable to create new borrow entry", exception.getMessage());
      Map<String, String> violations = new HashMap<>();
      violations.put("userId", "There is no user with user id " + request.getUserId());
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(userRepository, times(1)).findUserById(1L);
      verify(bookRepository, never()).getBookById(any());
      sch.verify(SecurityContextHolder::getContext, never());
      verify(securityContext, never()).getAuthentication();
      verify(authentication, never()).getAuthorities();
      verify(borrowEntryRepository, never()).countOpenedBorrowEntriesByBook(any());
      verify(borrowEntryRepository, never()).listRequestedBorrowEntriesByUser(any(), any());
      verify(borrowEntryRepository, never()).save(any());
    }

    log.info("Passed - Create borrow entry when user can't be found");
  }

  @Test
  public void testCreateBorrowEntryBookNotFoundFlow() {
    log.info("Test running - Create borrow entry when book can't be found...");

    try (
        MockedStatic<LocalDate> ld = mockStatic(LocalDate.class);
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class);
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      ld.when(LocalDate::now).thenReturn(mockLocalDateNow);

      var request = CreateBorrowEntryDto.builder()
          .userId(mockUser.getId())
          .bookId(101L)
          .status("Issued")
          .borrowDate(LocalDate.now().plusDays(1))
          .returnDate(LocalDate.now().plusDays(2))
          .build();

      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(userRepository.findUserById(mockUser.getId())).thenReturn(Optional.of(mockUser));
      when(bookRepository.getBookById(101L)).thenReturn(Optional.empty());

      var exception = assertThrows(BadRequestDetailsException.class,
          () -> borrowEntryService.createBorrowEntry(request));
      assertEquals("Unable to create new borrow entry", exception.getMessage());
      Map<String, String> violations = new HashMap<>();
      violations.put("bookId", "There is no book with id " + request.getBookId());
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(userRepository, times(1)).findUserById(mockUser.getId());
      verify(bookRepository, times(1)).getBookById(101L);
      sch.verify(SecurityContextHolder::getContext, never());
      verify(securityContext, never()).getAuthentication();
      verify(authentication, never()).getAuthorities();
      verify(borrowEntryRepository, never()).countOpenedBorrowEntriesByBook(any());
      verify(borrowEntryRepository, never()).listRequestedBorrowEntriesByUser(any(), any());
      verify(borrowEntryRepository, never()).save(any());
    }

    log.info("Passed - Create borrow entry when book can't be found");
  }

  @Test
  public void testCreateBorrowEntryLateRequestedBorrowEntryFlow() {
    log.info("Test running - Create borrow entry when user try to request book with invalid borrow date...");

    try (
        MockedStatic<LocalDate> ld = mockStatic(LocalDate.class);
        MockedStatic<LocalDateTime> ldt = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class);
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      ld.when(LocalDate::now).thenReturn(mockLocalDateNow);
      ldt.when(LocalDateTime::now).thenReturn(mockLocalDateTimeNow);

      Book mockThesis = Book.builder()
          .id(101L)
          .title("Another Book Title")
          .authors("Author")
          .publisher("Publisher")
          .type(BookType.builder().name("Thesis").build())
          .field(BookField.builder().name("Field").build())
          .year(2023)
          .edition(1)
          .isbn("123-4-567890-12-4")
          .inventoryNumber("101 CSST TH 2023")
          .coverImage("./src/main/resources/covers/101.jpg")
          .quantity(1)
          .build();

      var request = CreateBorrowEntryDto.builder()
          .userId(mockUser.getId())
          .bookId(mockThesis.getId())
          .status("Requested")
          .borrowDate(LocalDate.now())
          .returnDate(LocalDate.now().plusDays(1))
          .build();

      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(userRepository.findUserById(mockUser.getId())).thenReturn(Optional.of(mockUser));
      when(bookRepository.getBookById(mockThesis.getId())).thenReturn(Optional.of(mockThesis));
      sch.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getAuthorities()).thenReturn(Collections.of(new SimpleGrantedAuthority("ROLE_USER")));

      var exception = assertThrows(BadRequestDetailsException.class,
          () -> borrowEntryService.createBorrowEntry(request));
      assertEquals("Unable to create new borrow entry", exception.getMessage());
      Map<String, String> violations = new HashMap<>();
      violations.put("borrowDate", "Borrow date must be in the future");
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(userRepository, times(1)).findUserById(mockUser.getId());
      verify(bookRepository, times(1)).getBookById(101L);
      sch.verify(SecurityContextHolder::getContext, times(1));
      verify(securityContext, times(1)).getAuthentication();
      verify(authentication, times(1)).getAuthorities();
      verify(borrowEntryRepository, never()).countOpenedBorrowEntriesByBook(any());
      verify(borrowEntryRepository, never()).listRequestedBorrowEntriesByUser(any(), any());
      verify(borrowEntryRepository, never()).save(any());
    }

    log.info("Passed - Create borrow entry when user try to request book with invalid borrow date");
  }

  @Test
  public void testCreateBorrowEntryRequestedBookNotAvailableFlow() {
    log.info("Test running - Create borrow entry when book is unavailable borrow...");

    mockBorrowEntry.setStatus(BorrowStatus.Issued);

    var request = CreateBorrowEntryDto.builder()
        .userId(mockUser.getId())
        .bookId(mockBook.getId())
        .status("Requested")
        .borrowDate(LocalDate.now().plusDays(1))
        .returnDate(LocalDate.now().plusDays(2))
        .build();

    try (
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class);
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(userRepository.findUserById(mockUser.getId())).thenReturn(Optional.of(mockUser));
      when(bookRepository.getBookById(mockBook.getId())).thenReturn(Optional.of(mockBook));
      sch.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getAuthorities()).thenReturn(Collections.of(new SimpleGrantedAuthority("ROLE_USER")));
      when(borrowEntryRepository.countOpenedBorrowEntriesByBook(mockBook)).thenReturn(1);

      var exception = assertThrows(BadRequestDetailsException.class,
          () -> borrowEntryService.createBorrowEntry(request));
      assertEquals("Unable to create new borrow entry", exception.getMessage());
      Map<String, String> violations = new HashMap<>();
      violations.put("bookId", "Book with id " + request.getBookId() + " is not available to borrow");
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(userRepository, times(1)).findUserById(mockUser.getId());
      verify(bookRepository, times(1)).getBookById(mockBook.getId());
      sch.verify(SecurityContextHolder::getContext, times(1));
      verify(securityContext, times(1)).getAuthentication();
      verify(authentication, times(1)).getAuthorities();
      verify(borrowEntryRepository, times(1)).countOpenedBorrowEntriesByBook(any());
      verify(borrowEntryRepository, never()).listRequestedBorrowEntriesByUser(
          mockUser, mockBook);
      verify(borrowEntryRepository, never()).save(any());
    }

    log.info("Passed - Create borrow entry when book is unavailable borrow");
  }

  @Test
  public void testCreateBorrowEntryRequestedBorrowEntryExistsFlow() {
    log.info("Test running - Create borrow entry when user creates a borrow entry but another entry with same book i" +
        "s already exists...");

    var request = CreateBorrowEntryDto.builder()
        .userId(mockUser.getId())
        .bookId(mockBook.getId())
        .status("Requested")
        .borrowDate(LocalDate.now().plusDays(1))
        .returnDate(LocalDate.now().plusDays(2))
        .build();

    try (
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class);
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(userRepository.findUserById(mockUser.getId())).thenReturn(Optional.of(mockUser));
      when(bookRepository.getBookById(mockBook.getId())).thenReturn(Optional.of(mockBook));
      sch.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getAuthorities()).thenReturn(Collections.of(new SimpleGrantedAuthority("ROLE_USER")));
      when(borrowEntryRepository.countOpenedBorrowEntriesByBook(mockBook)).thenReturn(0);
      when(borrowEntryRepository.listRequestedBorrowEntriesByUser(mockUser, mockBook)).thenReturn(
          List.of(mockBorrowEntry));

      var exception = assertThrows(BadRequestDetailsException.class,
          () -> borrowEntryService.createBorrowEntry(request));
      assertEquals("Unable to create new borrow entry", exception.getMessage());
      Map<String, String> violations = new HashMap<>();
      violations.put("status", "You already requested to borrow this book");
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(userRepository, times(1)).findUserById(mockUser.getId());
      verify(bookRepository, times(1)).getBookById(mockBook.getId());
      sch.verify(SecurityContextHolder::getContext, times(1));
      verify(securityContext, times(1)).getAuthentication();
      verify(authentication, times(1)).getAuthorities();
      verify(borrowEntryRepository, times(1)).countOpenedBorrowEntriesByBook(mockBook);
      verify(borrowEntryRepository, times(1)).listRequestedBorrowEntriesByUser(
          mockUser, mockBook);
      verify(borrowEntryRepository, never()).save(any());
    }

    log.info("Passed - Create borrow entry when user creates a borrow entry but another entry with same book is " +
        "already exists");
  }

  @Test
  public void testUpdateBorrowEntryHappyFlow() {
    log.info("Test running - Update borrow entry happy flow...");

    try (
        MockedStatic<LocalDate> ld = mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS);
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      ld.when(LocalDate::now).thenReturn(mockLocalDateNow);

      User mockUser2 = User.builder()
          .id(1L)
          .email("anotherUser@email.com")
          .password("testEncodedPassword")
          .firstName("Another")
          .lastName("Name")
          .role(Role.USER)
          .createdDate(LocalDate.now())
          .enabled(false)
          .build();

      Book mockThesis = Book.builder()
          .id(101L)
          .title("Another Book Title")
          .authors("Author")
          .publisher("Publisher")
          .type(BookType.builder().name("Thesis").build())
          .field(BookField.builder().name("Field").build())
          .year(2023)
          .edition(1)
          .isbn("123-4-567890-12-4")
          .inventoryNumber("101 CSST TH 2023")
          .coverImage("./src/main/resources/covers/101.jpg")
          .quantity(1)
          .build();

      var request = UpdateBorrowEntryDto.builder()
          .borrowEntryId(0L)
          .userId(mockUser2.getId())
          .bookId(mockThesis.getId())
          .status("Issued")
          .borrowDate(LocalDate.now().plusDays(1))
          .returnDate(LocalDate.now().plusDays(2))
          .build();

      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(borrowEntryRepository.getBorrowEntryById(request.getBorrowEntryId())).thenReturn(
          Optional.of(mockBorrowEntry));
      when(userRepository.findUserById(request.getUserId())).thenReturn(Optional.of(mockUser2));
      when(bookRepository.getBookById(request.getBookId())).thenReturn(Optional.of(mockThesis));

      borrowEntryService.updateBorrowEntryById(request);

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(borrowEntryRepository, times(1)).getBorrowEntryById(request.getBorrowEntryId());
      verify(userRepository, times(1)).findUserById(mockUser2.getId());
      verify(bookRepository, times(1)).getBookById(mockThesis.getId());
      verify(borrowEntryRepository, times(1)).updateBorrowEntryById(request.getBorrowEntryId(),
          mockUser2, mockThesis, request.getBorrowDate(), request.getReturnDate(), BorrowStatus.Issued);
    }

    log.info("Passed - Update borrow entry happy flow");
  }

  @Test
  public void testUpdateBorrowEntryInvalidInputsFlow() {
    log.info("Test running - Update borrow entry when inputs are invalid...");

    try (
        MockedStatic<LocalDate> ld = mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS);
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      ld.when(LocalDate::now).thenReturn(mockLocalDateNow);

      var request = UpdateBorrowEntryDto.builder()
          .borrowEntryId(0L)
          .userId(mockUser.getId())
          .bookId(mockBook.getId())
          .status("Invalid input")
          .borrowDate(LocalDate.now().plusDays(1))
          .returnDate(LocalDate.now().plusDays(2))
          .build();

      Map<String, String> violations = new HashMap<>();
      violations.put("status", "Borrow status must either be Requested, Issued, Returned, Overdue or Lost");
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(violations);

      var exception = assertThrows(BadRequestDetailsException.class,
          () -> borrowEntryService.updateBorrowEntryById(request));
      assertEquals("Unable to update borrow entry by id", exception.getMessage());
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(borrowEntryRepository, never()).getBorrowEntryById(any());
      verify(userRepository, never()).findUserById(any());
      verify(bookRepository, never()).getBookById(any());
      verify(borrowEntryRepository, never()).updateBorrowEntryById(any(), any(), any(), any(), any(), any());
    }

    log.info("Passed - Update borrow entry when inputs are invalid");
  }

  @Test
  public void testUpdateBorrowEntryBorrowEntryNotFoundFlow() {
    log.info("Test running - Update borrow entry when borrow entry can't be found...");

    try (
        MockedStatic<LocalDate> ld = mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS);
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      ld.when(LocalDate::now).thenReturn(mockLocalDateNow);

      var request = UpdateBorrowEntryDto.builder()
          .borrowEntryId(0L)
          .userId(mockUser.getId())
          .bookId(mockBook.getId())
          .status("Issued")
          .borrowDate(LocalDate.now().plusDays(1))
          .returnDate(LocalDate.now().plusDays(2))
          .build();

      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(borrowEntryRepository.getBorrowEntryById(mockBorrowEntry.getId())).thenReturn(Optional.empty());

      var exception = assertThrows(BadRequestDetailsException.class,
          () -> borrowEntryService.updateBorrowEntryById(request));
      assertEquals("Unable to update borrow entry by id", exception.getMessage());
      Map<String, String> violations = new HashMap<>();
      violations.put("borrowEntryId", "Borrow entry with id " + request.getBorrowEntryId() + " is not exists");
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(borrowEntryRepository, times(1)).getBorrowEntryById(mockBorrowEntry.getId());
      verify(userRepository, never()).findUserById(any());
      verify(bookRepository, never()).getBookById(any());
      verify(borrowEntryRepository, never()).updateBorrowEntryById(any(), any(), any(), any(), any(), any());
    }

    log.info("Passed - Update borrow entry when borrow entry can't be found");
  }

  @Test
  public void testUpdateBorrowEntryUserNotFoundFlow() {
    log.info("Test running - Update borrow entry when user can't be found...");

    try (
        MockedStatic<LocalDate> ld = mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS);
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      ld.when(LocalDate::now).thenReturn(mockLocalDateNow);

      var request = UpdateBorrowEntryDto.builder()
          .borrowEntryId(0L)
          .userId(mockUser.getId())
          .bookId(mockBook.getId())
          .status("Issued")
          .borrowDate(LocalDate.now().plusDays(1))
          .returnDate(LocalDate.now().plusDays(2))
          .build();

      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(borrowEntryRepository.getBorrowEntryById(mockBorrowEntry.getId())).thenReturn(Optional.of(mockBorrowEntry));
      when(userRepository.findUserById(mockUser.getId())).thenReturn(Optional.empty());

      var exception = assertThrows(BadRequestDetailsException.class,
          () -> borrowEntryService.updateBorrowEntryById(request));
      assertEquals("Unable to update borrow entry by id", exception.getMessage());
      Map<String, String> violations = new HashMap<>();
      violations.put("userId", "There is no user with username " + request.getUserId());
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(borrowEntryRepository, times(1)).getBorrowEntryById(mockBorrowEntry.getId());
      verify(userRepository, times(1)).findUserById(mockUser.getId());
      verify(bookRepository, never()).getBookById(any());
      verify(borrowEntryRepository, never()).updateBorrowEntryById(any(), any(), any(), any(), any(), any());
    }

    log.info("Passed - Update borrow entry when user can't be found");
  }

  @Test
  public void testUpdateBorrowEntryBookNotFoundFlow() {
    log.info("Test running - Update borrow entry when book can't be found...");

    try (
        MockedStatic<LocalDate> ld = mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS);
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      ld.when(LocalDate::now).thenReturn(mockLocalDateNow);

      var request = UpdateBorrowEntryDto.builder()
          .borrowEntryId(0L)
          .userId(mockUser.getId())
          .bookId(mockBook.getId())
          .status("Issued")
          .borrowDate(LocalDate.now().plusDays(1))
          .returnDate(LocalDate.now().plusDays(2))
          .build();

      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(borrowEntryRepository.getBorrowEntryById(mockBorrowEntry.getId())).thenReturn(Optional.of(mockBorrowEntry));
      when(userRepository.findUserById(mockUser.getId())).thenReturn(Optional.of(mockUser));
      when(bookRepository.getBookById(mockBook.getId())).thenReturn(Optional.empty());

      var exception = assertThrows(BadRequestDetailsException.class,
          () -> borrowEntryService.updateBorrowEntryById(request));
      assertEquals("Unable to update borrow entry by id", exception.getMessage());
      Map<String, String> violations = new HashMap<>();
      violations.put("bookId", "There is no book with id " + request.getBookId());
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(borrowEntryRepository, times(1)).getBorrowEntryById(mockBorrowEntry.getId());
      verify(userRepository, times(1)).findUserById(mockUser.getId());
      verify(bookRepository, times(1)).getBookById(mockBook.getId());
      verify(borrowEntryRepository, never()).updateBorrowEntryById(any(), any(), any(), any(), any(), any());
    }

    log.info("Passed - Update borrow entry when book can't be found");
  }

  @Test
  public void testUpdateBorrowEntryInvalidReturnDateFlow() {
    log.info("Test running - Update borrow entry when return date is invalid...");

    try (
        MockedStatic<LocalDate> ld = mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS);
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      ld.when(LocalDate::now).thenReturn(mockLocalDateNow);

      var request = UpdateBorrowEntryDto.builder()
          .borrowEntryId(0L)
          .userId(mockUser.getId())
          .bookId(mockBook.getId())
          .status("Issued")
          .borrowDate(LocalDate.now())
          .returnDate(LocalDate.now())
          .build();

      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(borrowEntryRepository.getBorrowEntryById(mockBorrowEntry.getId())).thenReturn(Optional.of(mockBorrowEntry));
      when(userRepository.findUserById(mockUser.getId())).thenReturn(Optional.of(mockUser));
      when(bookRepository.getBookById(mockBook.getId())).thenReturn(Optional.of(mockBook));

      var exception = assertThrows(BadRequestDetailsException.class,
          () -> borrowEntryService.updateBorrowEntryById(request));
      assertEquals("Unable to update borrow entry by id", exception.getMessage());
      Map<String, String> violations = new HashMap<>();
      violations.put("returnDate", "Return date must be in the future");
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(borrowEntryRepository, times(1)).getBorrowEntryById(mockBorrowEntry.getId());
      verify(userRepository, times(1)).findUserById(mockUser.getId());
      verify(bookRepository, times(1)).getBookById(mockBook.getId());
      verify(borrowEntryRepository, never()).updateBorrowEntryById(any(), any(), any(), any(), any(), any());
    }

    log.info("Passed - Update borrow entry when return date is invalid");
  }

  @Test
  public void testDeleteBorrowEntryByIdHappyFlow() {
    log.info("Test running - Delete borrow entry by id happy flow...");

    borrowEntryService.deleteBorrowEntryById(mockBook.getId());

    verify(borrowEntryRepository, times(1)).deleteBorrowEntryById(mockBook.getId());

    log.info("Passed - Delete borrow entry by id happy flow...");
  }
}
