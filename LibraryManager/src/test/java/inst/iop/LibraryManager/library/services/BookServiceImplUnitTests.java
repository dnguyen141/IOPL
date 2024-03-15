package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.library.dtos.CreateBookDto;
import inst.iop.LibraryManager.library.dtos.ListAllBooksDto;
import inst.iop.LibraryManager.library.dtos.SearchBooksDto;
import inst.iop.LibraryManager.library.dtos.UpdateBookDto;
import inst.iop.LibraryManager.library.entities.Book;
import inst.iop.LibraryManager.library.entities.BookField;
import inst.iop.LibraryManager.library.entities.BookType;
import inst.iop.LibraryManager.library.repositories.BookRepository;
import inst.iop.LibraryManager.library.repositories.BorrowEntryRepository;
import inst.iop.LibraryManager.library.services.BookFieldService;
import inst.iop.LibraryManager.library.services.BookServiceImpl;
import inst.iop.LibraryManager.library.services.BookTypeService;
import inst.iop.LibraryManager.library.services.ImageFileService;
import inst.iop.LibraryManager.utilities.ConstraintViolationSetHandler;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
public class BookServiceImplUnitTests {

  @Mock
  private BookRepository bookRepository;

  @Mock
  private BorrowEntryRepository borrowEntryRepository;

  @Mock
  private BookTypeService bookTypeService;

  @Mock
  private BookFieldService bookFieldService;

  @Mock
  private ImageFileService imageFileService;

  @Mock
  private Validator validator;

  @InjectMocks
  private BookServiceImpl bookService;

  private Book book;

  private static final int mockPageNumber = 0;

  private static final int mockPageSize = 10;

  private static final int mockBeforeYear = 2024;

  private static final int mockAfterYear = 1900;

  @BeforeEach
  public void setUp() {
    book = Book.builder()
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
  }

  @Test
  public void testListAllBooksHappyFlow() {
    log.info("Test running - List all books happy flow...");

    var request = new ListAllBooksDto(mockPageNumber, mockPageSize);
    var expected = new PageImpl<>(List.of(book));

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(bookRepository.listAllBooks(PageRequest.of(mockPageNumber, mockPageSize))).thenReturn(expected);

      assertEquals(expected, bookService.listAllBooks(request));

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, times(1)).listAllBooks(PageRequest.of(mockPageNumber, mockPageSize));
    }

    log.info("Passed - List all books happy flow");
  }

  @Test
  public void testListAllBooksInvalidInputsFlow() {
    log.info("Test running - List all books when inputs are invalid...");

    var request = new ListAllBooksDto(-1, 0);
    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      Map<String, String> violations = new HashMap<>();
      violations.put("pageNumber", "Page number must be an integer that is bigger than 0");
      violations.put("pageSize", "Page size must be an integer of at least 1");
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(violations);

      BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
          () -> bookService.listAllBooks(request));
      assertEquals("Invalid list all books request", exception.getMessage());
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, never()).listAllBooks(any());
    }

    log.info("Passed - List all books when inputs are invalid");
  }

  @Test
  public void testFindBookByIdHappyFlow() {
    log.info("Test running - Find book by id happy flow...");

    when(bookRepository.getBookById(book.getId())).thenReturn(Optional.of(book));
    assertEquals(book, bookService.findBookById(book.getId()));
    verify(bookRepository, times(1)).getBookById(book.getId());

    log.info("Passed - Find book by id happy flow");
  }

  @Test
  public void testFindBookByIdBookNotFoundFlow() {
    log.info("Test running - Find book by id when book can't be found...");

    when(bookRepository.getBookById(book.getId())).thenReturn(Optional.empty());

    Map<String, String> violations = new HashMap<>();
    violations.put("id", "There is no book with id " + book.getId());
    BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
        () -> bookService.findBookById(book.getId()));
    assertEquals("Unable to get book with id " + book.getId(), exception.getMessage());
    assertEquals(violations, exception.getViolations());
    verify(bookRepository, times(1)).getBookById(book.getId());

    log.info("Passed - Find book by id when book can't be found");
  }

  @Test
  public void testFindBooksHappyFlow() {
    log.info("Test running - Find books using search term happy flow...");

    var request = new SearchBooksDto(
        "bo",
        mockBeforeYear,
        mockAfterYear,
        mockPageNumber,
        mockPageSize
    );

    Book book1 = Book.builder()
        .id(101L)
        .title("Book Title 1")
        .authors("Book")
        .publisher("Publisher")
        .type(BookType.builder().name("Book").build())
        .field(BookField.builder().name("Field").build())
        .year(2023)
        .edition(1)
        .isbn("123-4-567890-12-4")
        .inventoryNumber("101 CSST BO 2023")
        .coverImage(null)
        .quantity(1)
        .build();

    Book book2 = Book.builder()
        .id(102L)
        .title("Book Title 2")
        .authors("Author")
        .publisher("Book")
        .type(BookType.builder().name("Book").build())
        .field(BookField.builder().name("Field").build())
        .year(2023)
        .edition(1)
        .isbn("123-4-567890-12-5")
        .inventoryNumber("102 CSST BO 2023")
        .coverImage(null)
        .quantity(1)
        .build();

    Book book3 = Book.builder()
        .id(103L)
        .title("Book Title 3")
        .authors("Author")
        .publisher("Publisher")
        .type(BookType.builder().name("Book").build())
        .field(BookField.builder().name("Book").build())
        .year(2023)
        .edition(1)
        .isbn("123-4-567890-12-6")
        .inventoryNumber("103 CSST BO 2023")
        .coverImage(null)
        .quantity(1)
        .build();

    Book book4 = Book.builder()
        .id(104L)
        .title("Book Title 4")
        .authors("Author")
        .publisher("Publisher")
        .type(BookType.builder().name("Book").build())
        .field(BookField.builder().name("Field").build())
        .year(2023)
        .edition(1)
        .isbn("book")
        .inventoryNumber("104 CSST BO 2023")
        .coverImage(null)
        .quantity(1)
        .build();

    Book book5 = Book.builder()
        .id(105L)
        .title("Book Title 4")
        .authors("Author")
        .publisher("Publisher")
        .type(BookType.builder().name("Book").build())
        .field(BookField.builder().name("Field").build())
        .year(2023)
        .edition(1)
        .isbn("123-4-567890-12-8")
        .inventoryNumber("105 CSST BO 2023")
        .coverImage(null)
        .quantity(1)
        .build();

    var expected = new PageImpl<>(List.of(book, book1, book2, book3, book4, book5));

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(bookRepository.findBooks(request.getTerm(), request.getTerm(), request.getTerm(), request.getTerm(),
          request.getTerm(), request.getBeforeYear(), request.getAfterYear(), request.getTerm(),
          request.getTerm(), PageRequest.of(request.getPageNumber(), request.getPageSize())))
          .thenReturn(expected);

      assertEquals(expected, bookService.findBooks(request));

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, times(1)).findBooks(request.getTerm(), request.getTerm(),
          request.getTerm(), request.getTerm(), request.getTerm(), request.getBeforeYear(), request.getAfterYear(),
          request.getTerm(), request.getTerm(), PageRequest.of(request.getPageNumber(), request.getPageSize()));
    }

    log.info("Passed - Find books using search term happy flow");
  }

  @Test
  public void testFindBooksInputsInvalidFlow() {
    log.info("Test running - Find books by title when inputs are invalid...");

    var request = new SearchBooksDto(
        "Normal term",
        2025,
        2026,
        -1,
        0
    );

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      Map<String, String> violations = new HashMap<>();
      violations.put("beforeYear", "Year must be equal or smaller than current year");
      violations.put("afterYear", "Year must be equal or smaller than current year");
      violations.put("pageNumber", "Page number must be an integer that is bigger than 0");
      violations.put("pageSize", "Page size must be an integer of at least 1");
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(violations);

      var exception = assertThrows(BadRequestDetailsException.class, () -> bookService.findBooks(request));
      assertEquals("Invalid search books by term request", exception.getMessage());
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, never()).findBooks(any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    log.info("Passed - Find books by title when inputs are invalid");
  }

  @Test
  public void testFindBooksByTitleHappyFlow() {
    log.info("Test running - Find books by title using search term happy flow...");

    var request = new SearchBooksDto(
        "Book Title",
        mockBeforeYear,
        mockAfterYear,
        mockPageNumber,
        mockPageSize
    );

    var expected = new PageImpl<>(List.of(book));

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(bookRepository.findBooksByTitle(request.getTerm(), request.getBeforeYear(), request.getAfterYear(),
          PageRequest.of(request.getPageNumber(), request.getPageSize()))).thenReturn(expected);

      assertEquals(expected, bookService.findBooksByTitle(request));

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, times(1)).findBooksByTitle(request.getTerm(),
          request.getBeforeYear(), request.getAfterYear(),
          PageRequest.of(request.getPageNumber(), request.getPageSize()));
    }

    log.info("Passed - Find books using search term happy flow");
  }

  @Test
  public void testFindBooksByTitleInputsInvalidFlow() {
    log.info("Test running - Find books by title when inputs are invalid...");

    var request = new SearchBooksDto(
        "Book title",
        2025,
        2026,
        -1,
        0
    );

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      Map<String, String> violations = new HashMap<>();
      violations.put("beforeYear", "Year must be equal or smaller than current year");
      violations.put("afterYear", "Year must be equal or smaller than current year");
      violations.put("pageNumber", "Page number must be an integer that is bigger than 0");
      violations.put("pageSize", "Page size must be an integer of at least 1");
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(violations);

      var exception = assertThrows(BadRequestDetailsException.class, () -> bookService.findBooksByTitle(request));
      assertEquals("Invalid search books by title request", exception.getMessage());
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, never()).findBooksByTitle(any(), any(), any(), any());
    }

    log.info("Passed - Find books by title when inputs are invalid");
  }

  @Test
  public void testFindBooksByAuthorHappyFlow() {
    log.info("Test running - Find books by authors happy flow...");

    var request = new SearchBooksDto(
        "Author",
        mockBeforeYear,
        mockAfterYear,
        mockPageNumber,
        mockPageSize
    );

    var expected = new PageImpl<>(List.of(book));

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(bookRepository.findBooksByAuthors(request.getTerm(), request.getBeforeYear(), request.getAfterYear(),
          PageRequest.of(request.getPageNumber(), request.getPageSize()))).thenReturn(expected);

      assertEquals(expected, bookService.findBooksByAuthors(request));

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, times(1)).findBooksByAuthors(request.getTerm(),
          request.getBeforeYear(), request.getAfterYear(),
          PageRequest.of(request.getPageNumber(), request.getPageSize()));
    }

    log.info("Passed - Find books by authors happy flow");
  }

  @Test
  public void testFindBooksByAuthorInputsInvalidFlow() {
    log.info("Test running - Find books by authors when inputs are invalid...");

    var request = new SearchBooksDto(
        "Author",
        2025,
        2026,
        -1,
        0
    );

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      Map<String, String> violations = new HashMap<>();
      violations.put("beforeYear", "Year must be equal or smaller than current year");
      violations.put("afterYear", "Year must be equal or smaller than current year");
      violations.put("pageNumber", "Page number must be an integer that is bigger than 0");
      violations.put("pageSize", "Page size must be an integer of at least 1");
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(violations);

      var exception = assertThrows(BadRequestDetailsException.class, () -> bookService.findBooksByAuthors(request));
      assertEquals("Invalid search books by authors request", exception.getMessage());
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, never()).findBooksByAuthors(any(), any(), any(), any());
    }

    log.info("Passed - Find books by authors when inputs are invalid");
  }

  @Test
  public void testFindBooksByPublisherHappyFlow() {
    log.info("Test running - Find books by publisher happy flow...");

    var request = new SearchBooksDto(
        "Publisher",
        mockBeforeYear,
        mockAfterYear,
        mockPageNumber,
        mockPageSize
    );

    var expected = new PageImpl<>(List.of(book));

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(bookRepository.findBooksByPublisher(request.getTerm(), request.getBeforeYear(), request.getAfterYear(),
          PageRequest.of(request.getPageNumber(), request.getPageSize()))).thenReturn(expected);

      assertEquals(expected, bookService.findBooksByPublisher(request));

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, times(1)).findBooksByPublisher(request.getTerm(),
          request.getBeforeYear(), request.getAfterYear(),
          PageRequest.of(request.getPageNumber(), request.getPageSize()));
    }

    log.info("Passed - Find books by publisher happy flow");
  }

  @Test
  public void testFindBooksByPublisherInputsInvalidFlow() {
    log.info("Test running - Find books by publisher when inputs are invalid...");

    var request = new SearchBooksDto(
        "Publisher",
        2025,
        2026,
        -1,
        0
    );

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      Map<String, String> violations = new HashMap<>();
      violations.put("beforeYear", "Year must be equal or smaller than current year");
      violations.put("afterYear", "Year must be equal or smaller than current year");
      violations.put("pageNumber", "Page number must be an integer that is bigger than 0");
      violations.put("pageSize", "Page size must be an integer of at least 1");
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(violations);

      var exception = assertThrows(BadRequestDetailsException.class, () -> bookService.findBooksByPublisher(request));
      assertEquals("Invalid search books by publisher request", exception.getMessage());
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, never()).findBooksByPublisher(any(), any(), any(), any());
    }

    log.info("Passed - Find books by authors when inputs are invalid");
  }

  @Test
  public void testFindBooksByTypeHappyFlow() {
    log.info("Test running - Find books by type happy flow...");

    var request = new SearchBooksDto(
        "Book",
        mockBeforeYear,
        mockAfterYear,
        mockPageNumber,
        mockPageSize
    );

    var expected = new PageImpl<>(List.of(book));

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(bookRepository.findBooksByType(request.getTerm(), request.getBeforeYear(), request.getAfterYear(),
          PageRequest.of(request.getPageNumber(), request.getPageSize()))).thenReturn(expected);

      assertEquals(expected, bookService.findBooksByType(request));

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, times(1)).findBooksByType(request.getTerm(),
          request.getBeforeYear(), request.getAfterYear(),
          PageRequest.of(request.getPageNumber(), request.getPageSize()));
    }

    log.info("Passed - Find books by type happy flow");
  }

  @Test
  public void testFindBooksByTypeInputsInvalidFlow() {
    log.info("Test running - Find books by type when inputs are invalid...");

    var request = new SearchBooksDto(
        "Book",
        2025,
        2026,
        -1,
        0
    );

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      Map<String, String> violations = new HashMap<>();
      violations.put("beforeYear", "Year must be equal or smaller than current year");
      violations.put("afterYear", "Year must be equal or smaller than current year");
      violations.put("pageNumber", "Page number must be an integer that is bigger than 0");
      violations.put("pageSize", "Page size must be an integer of at least 1");
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(violations);

      var exception = assertThrows(BadRequestDetailsException.class, () -> bookService.findBooksByType(request));
      assertEquals("Invalid search books by type request", exception.getMessage());
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, never()).findBooksByType(any(), any(), any(), any());
    }

    log.info("Passed - Find books by type when inputs are invalid");
  }

  @Test
  public void testFindBooksByFieldHappyFlow() {
    log.info("Test running - Find books by field happy flow...");

    var request = new SearchBooksDto(
        "Field",
        mockBeforeYear,
        mockAfterYear,
        mockPageNumber,
        mockPageSize
    );

    var expected = new PageImpl<>(List.of(book));

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(bookRepository.findBooksByField(request.getTerm(), request.getBeforeYear(), request.getAfterYear(),
          PageRequest.of(request.getPageNumber(), request.getPageSize()))).thenReturn(expected);

      assertEquals(expected, bookService.findBooksByField(request));

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, times(1)).findBooksByField(request.getTerm(),
          request.getBeforeYear(), request.getAfterYear(),
          PageRequest.of(request.getPageNumber(), request.getPageSize()));
    }

    log.info("Passed - Find books by field happy flow");
  }

  @Test
  public void testFindBooksByFieldInputsInvalidFlow() {
    log.info("Test running - Find books by field when inputs are invalid...");

    var request = new SearchBooksDto(
        "Field",
        2025,
        2026,
        -1,
        0
    );

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      Map<String, String> violations = new HashMap<>();
      violations.put("beforeYear", "Year must be equal or smaller than current year");
      violations.put("afterYear", "Year must be equal or smaller than current year");
      violations.put("pageNumber", "Page number must be an integer that is bigger than 0");
      violations.put("pageSize", "Page size must be an integer of at least 1");
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(violations);

      var exception = assertThrows(BadRequestDetailsException.class, () -> bookService.findBooksByField(request));
      assertEquals("Invalid search books by field request", exception.getMessage());
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, never()).findBooksByField(any(), any(), any(), any());
    }

    log.info("Passed - Find books by field when inputs are invalid");
  }

  @Test
  public void testFindBooksByIsbnHappyFlow() {
    log.info("Test running - Find books by isbn happy flow...");

    var request = new SearchBooksDto(
        "123-4-567890-12-3",
        mockBeforeYear,
        mockAfterYear,
        mockPageNumber,
        mockPageSize
    );

    var expected = new PageImpl<>(List.of(book));

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(bookRepository.findBooksByIsbn(request.getTerm(), request.getBeforeYear(), request.getAfterYear(),
          PageRequest.of(request.getPageNumber(), request.getPageSize()))).thenReturn(expected);

      assertEquals(expected, bookService.findBooksByIsbn(request));

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, times(1)).findBooksByIsbn(request.getTerm(),
          request.getBeforeYear(), request.getAfterYear(),
          PageRequest.of(request.getPageNumber(), request.getPageSize()));
    }

    log.info("Passed - Find books by isbn happy flow");
  }

  @Test
  public void testFindBooksByIsbnInputsInvalidFlow() {
    log.info("Test running - Find books by isbn when inputs are invalid...");

    var request = new SearchBooksDto(
        "123-4-567890-12-3",
        2025,
        2026,
        -1,
        0
    );

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      Map<String, String> violations = new HashMap<>();
      violations.put("beforeYear", "Year must be equal or smaller than current year");
      violations.put("afterYear", "Year must be equal or smaller than current year");
      violations.put("pageNumber", "Page number must be an integer that is bigger than 0");
      violations.put("pageSize", "Page size must be an integer of at least 1");
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(violations);

      var exception = assertThrows(BadRequestDetailsException.class, () -> bookService.findBooksByIsbn(request));
      assertEquals("Invalid search books by isbn request",exception.getMessage());
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, never()).findBooksByIsbn(any(), any(), any(), any());
    }

    log.info("Passed - Find books by isbn when inputs are invalid");
  }

  @Test
  public void testFindBooksByInventoryNumberHappyFlow() {
    log.info("Test running - Find books by inventory number happy flow...");

    var request = new SearchBooksDto(
        "100 CSST BO 2023",
        mockBeforeYear,
        mockAfterYear,
        mockPageNumber,
        mockPageSize
    );

    var expected = new PageImpl<>(List.of(book));

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(bookRepository.findBooksByInventoryNumber(request.getTerm(), request.getBeforeYear(), request.getAfterYear(),
          PageRequest.of(request.getPageNumber(), request.getPageSize()))).thenReturn(expected);

      assertEquals(expected, bookService.findBooksByInventoryNumber(request));

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, times(1)).findBooksByInventoryNumber(request.getTerm(),
          request.getBeforeYear(), request.getAfterYear(),
          PageRequest.of(request.getPageNumber(), request.getPageSize()));
    }

    log.info("Passed - Find books by inventory number happy flow");
  }

  @Test
  public void testFindBooksByInventoryNumberInputsInvalidFlow() {
    log.info("Test running - Find books by inventory number when inputs are invalid...");

    var request = new SearchBooksDto(
        "100 CSST BO 2023",
        2025,
        2026,
        -1,
        0
    );

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      Map<String, String> violations = new HashMap<>();
      violations.put("beforeYear", "Year must be equal or smaller than current year");
      violations.put("afterYear", "Year must be equal or smaller than current year");
      violations.put("pageNumber", "Page number must be an integer that is bigger than 0");
      violations.put("pageSize", "Page size must be an integer of at least 1");
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(violations);

      var exception = assertThrows(BadRequestDetailsException.class,
          () -> bookService.findBooksByInventoryNumber(request));
      assertEquals("Invalid search books by inventory number request", exception.getMessage());
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, never()).findBooksByInventoryNumber(any(), any(), any(), any());
    }

    log.info("Passed - Find books by inventory number when inputs are invalid");
  }

  @Test
  public void testGetCoverImagePathHappyFlow() {
    log.info("Test running - Get cover image path happy flow...");

    when(bookRepository.getBookById(book.getId())).thenReturn(Optional.of(book));
    assertEquals("resources\\covers\\100.jpg", bookService.getCoverImagePath(book.getId()));
    verify(bookRepository, times(1)).getBookById(book.getId());

    log.info("Passed - Get cover image path happy flow");
  }

  @Test
  public void testGetCoverImagePathNoCoverImageFlow() {
    log.info("Test running - Get cover image path when book has no cover image...");

    book.setCoverImage(null);
    when(bookRepository.getBookById(book.getId())).thenReturn(Optional.of(book));
    assertNull(bookService.getCoverImagePath(book.getId()));
    verify(bookRepository, times(1)).getBookById(book.getId());

    log.info("Passed - Get cover image path when book has no cover image");
  }

  @Test
  public void testGetCoverImagePathBookNotFoundFlow() {
    log.info("Test running - Get cover image path when book can't be found...");

    when(bookRepository.getBookById(book.getId())).thenReturn(Optional.empty());

    var exception = assertThrows(BadRequestDetailsException.class, () -> bookService.getCoverImagePath(book.getId()));
    assertEquals("Unable to get book cover", exception.getMessage());
    Map<String, String> violations = new HashMap<>();
    violations.put("book", "Book with id " + book.getId() + " is not found");
    assertEquals(violations, exception.getViolations());

    verify(bookRepository, times(1)).getBookById(book.getId());

    log.info("Passed - Get cover image path when book can't be found");
  }

  @Test
  public void testCreateBookWithCoverImageUrlHappyFlow() {
    log.info("Test running - Create book with cover image url happy flow...");

    var request = CreateBookDto.builder()
        .title("Book Title 2")
        .authors("Author")
        .publisher("Publisher")
        .type("Book")
        .field("Field")
        .year(2023)
        .edition(1)
        .isbn("123-4-567890-12-4")
        .coverUrl("/link/to/cover.jpg")
        .quantity(1)
        .build();

    BookType bt = BookType.builder()
        .name("Book")
        .build();

    BookField bf = BookField.builder()
        .name("Field")
        .build();

    Book book2 = Book.builder()
        .title("Book Title 2")
        .authors("Author")
        .publisher("Publisher")
        .type(bt)
        .field(bf)
        .year(2023)
        .edition(1)
        .isbn("123-4-567890-12-4")
        .inventoryNumber("101 CSST BO 2023")
        .coverImage("./src/main/resources/covers/101.jpg")
        .quantity(1)
        .build();

    Book savedBook2 = Book.builder()
        .id(101L)
        .title("Book Title 2")
        .authors("Author")
        .publisher("Publisher")
        .type(bt)
        .field(bf)
        .year(2023)
        .edition(1)
        .isbn("123-4-567890-12-4")
        .inventoryNumber("101 CSST BO 2023")
        .coverImage("./src/main/resources/covers/101.jpg")
        .quantity(1)
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(bookRepository.findFirstByOrderById()).thenReturn(Optional.of(book));
      when(bookRepository.getCurrentBookIdSequenceValue()).thenReturn(100L);
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(bookTypeService.getBookTypeByString(request.getType().trim(), true))
          .thenReturn(bt);
      when(bookFieldService.getBookFieldByString(request.getField().trim(), true))
          .thenReturn(bf);
      when(bookRepository.getBookByTitle(request.getTitle().trim())).thenReturn(Optional.empty());
      when(imageFileService.downloadImage(request.getCoverUrl(), 101L)).thenReturn(
          "./src/main/resources/covers/101.jpg");
      when(bookRepository.save(book2)).thenAnswer(input -> {
        var book = (Book) input.getArgument(0);
        book.setId(101L);
        return book;
      });

      assertEquals(savedBook2, bookService.createBook(request, null));

      verify(bookRepository, times(1)).findFirstByOrderById();
      verify(bookRepository, times(1)).getCurrentBookIdSequenceValue();
      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookTypeService, times(1)).getBookTypeByString(request.getType().trim(),
          true);
      verify(bookFieldService, times(1)).getBookFieldByString(request.getField().trim(),
          true);
      verify(bookRepository, times(1)).getBookByTitle(request.getTitle().trim());
      verify(bookRepository, times(1)).save(any());
    }

    log.info("Passed - Create book with cover image url happy flow");
  }

  @Test
  public void testCreateBookWithUploadedCoverImageHappyFlow() {
    log.info("Test running - Create book with uploaded cover image happy flow...");

    var request = CreateBookDto.builder()
        .title("Book Title 2")
        .authors("Author")
        .publisher("Publisher")
        .type("Book")
        .field("Field")
        .year(2023)
        .edition(1)
        .isbn("123-4-567890-12-4")
        .coverUrl(null)
        .quantity(1)
        .build();

    BookType bt = BookType.builder()
        .name("Book")
        .build();

    BookField bf = BookField.builder()
        .name("Field")
        .build();

    Book book2 = Book.builder()
        .title("Book Title 2")
        .authors("Author")
        .publisher("Publisher")
        .type(bt)
        .field(bf)
        .year(2023)
        .edition(1)
        .isbn("123-4-567890-12-4")
        .inventoryNumber("101 CSST BO 2023")
        .coverImage("./src/main/resources/covers/101.jpg")
        .quantity(1)
        .build();

    Book savedBook2 = Book.builder()
        .id(101L)
        .title("Book Title 2")
        .authors("Author")
        .publisher("Publisher")
        .type(bt)
        .field(bf)
        .year(2023)
        .edition(1)
        .isbn("123-4-567890-12-4")
        .inventoryNumber("101 CSST BO 2023")
        .coverImage("./src/main/resources/covers/101.jpg")
        .quantity(1)
        .build();

    var mockCoverImageFile = new MockMultipartFile("123-4-567890-12-4.jpg", "mockCover.jpg",
        "image/jpeg", "This is a mock cover".getBytes());

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(bookRepository.findFirstByOrderById()).thenReturn(Optional.of(book));
      when(bookRepository.getCurrentBookIdSequenceValue()).thenReturn(100L);
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(bookTypeService.getBookTypeByString(request.getType().trim(), true))
          .thenReturn(bt);
      when(bookFieldService.getBookFieldByString(request.getField().trim(), true))
          .thenReturn(bf);
      when(bookRepository.getBookByTitle(request.getTitle().trim())).thenReturn(Optional.empty());
      when(imageFileService.uploadImage(mockCoverImageFile, 101L)).thenReturn(
          "./src/main/resources/covers/101.jpg");
      when(bookRepository.save(book2)).thenAnswer(input -> {
        var book = (Book) input.getArgument(0);
        book.setId(101L);
        return book;
      });

      assertEquals(savedBook2, bookService.createBook(request, mockCoverImageFile));

      verify(bookRepository, times(1)).findFirstByOrderById();
      verify(bookRepository, times(1)).getCurrentBookIdSequenceValue();
      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookTypeService, times(1)).getBookTypeByString(request.getType().trim(),
          true);
      verify(bookFieldService, times(1)).getBookFieldByString(request.getField().trim(),
          true);
      verify(bookRepository, times(1)).getBookByTitle(request.getTitle().trim());
      verify(bookRepository, times(1)).save(any());
    }

    log.info("Passed - Create book with uploaded cover image happy flow");
  }

  @Test
  public void testCreateBookWithNoCoverImageHappyFlow() {
    log.info("Test running - Create book with no cover image happy flow...");

    var request = CreateBookDto.builder()
        .title("Book Title 2")
        .authors("Author")
        .publisher("Publisher")
        .type("Book")
        .field("Field")
        .year(2023)
        .edition(1)
        .isbn("123-4-567890-12-4")
        .coverUrl(null)
        .quantity(1)
        .build();

    BookType bt = BookType.builder()
        .name("Book")
        .build();

    BookField bf = BookField.builder()
        .name("Field")
        .build();

    Book book2 = Book.builder()
        .title("Book Title 2")
        .authors("Author")
        .publisher("Publisher")
        .type(bt)
        .field(bf)
        .year(2023)
        .edition(1)
        .isbn("123-4-567890-12-4")
        .inventoryNumber("101 CSST BO 2023")
        .coverImage(null)
        .quantity(1)
        .build();

    Book savedBook2 = Book.builder()
        .id(101L)
        .title("Book Title 2")
        .authors("Author")
        .publisher("Publisher")
        .type(bt)
        .field(bf)
        .year(2023)
        .edition(1)
        .isbn("123-4-567890-12-4")
        .inventoryNumber("101 CSST BO 2023")
        .coverImage(null)
        .quantity(1)
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(bookRepository.findFirstByOrderById()).thenReturn(Optional.of(book));
      when(bookRepository.getCurrentBookIdSequenceValue()).thenReturn(100L);
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(bookTypeService.getBookTypeByString(request.getType().trim(), true))
          .thenReturn(bt);
      when(bookFieldService.getBookFieldByString(request.getField().trim(), true))
          .thenReturn(bf);
      when(bookRepository.getBookByTitle(request.getTitle().trim())).thenReturn(Optional.empty());
      when(bookRepository.save(book2)).thenAnswer(input -> {
        var book = (Book) input.getArgument(0);
        book.setId(101L);
        return book;
      });

      assertEquals(savedBook2, bookService.createBook(request, null));

      verify(bookRepository, times(1)).findFirstByOrderById();
      verify(bookRepository, times(1)).getCurrentBookIdSequenceValue();
      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookTypeService, times(1)).getBookTypeByString(request.getType().trim(),
          true);
      verify(bookFieldService, times(1)).getBookFieldByString(request.getField().trim(),
          true);
      verify(bookRepository, times(1)).getBookByTitle(request.getTitle().trim());
      verify(bookRepository, times(1)).save(any());
    }

    log.info("Passed - Create book with no cover image happy flow");
  }

  @Test
  public void testCreateBookInvalidInputsFlow() {
    log.info("Test running - Create book when inputs are invalid...");

    var request = CreateBookDto.builder()
        .title("")
        .authors("Author")
        .publisher("Publisher")
        .type("Book")
        .field("Field")
        .year(2023)
        .edition(1)
        .isbn("123-4-567890-12-4")
        .coverUrl(null)
        .quantity(1)
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(bookRepository.findFirstByOrderById()).thenReturn(Optional.of(book));

      Map<String, String> violations = new HashMap<>();
      violations.put("title", "Book title must have at least 1 character and at most 100 characters");
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(violations);

      var exception = assertThrows(BadRequestDetailsException.class, () ->
          bookService.createBook(request, null));
      assertEquals("Invalid create book request", exception.getMessage());
      assertEquals(violations, exception.getViolations());

      verify(bookRepository, times(1)).findFirstByOrderById();
      verify(bookRepository, times(1)).getCurrentBookIdSequenceValue();
      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookTypeService, never()).getBookTypeByString(anyString(), anyBoolean());
      verify(bookFieldService, never()).getBookFieldByString(anyString(), anyBoolean());
      verify(bookRepository, never()).save(any());
    }

    log.info("Passed - Create book when inputs are invalid");
  }

  @Test
  public void testCreateBookBookWithSameTitleExistedFlow() {
    log.info("Test running - Create book when book with same title existed...");

    var request = CreateBookDto.builder()
        .title("Book Title")
        .authors("Author")
        .publisher("Publisher")
        .type("Book")
        .field("Field")
        .year(2023)
        .edition(1)
        .isbn("123-4-567890-12-4")
        .coverUrl(null)
        .quantity(1)
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(bookRepository.findFirstByOrderById()).thenReturn(Optional.of(book));
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(bookRepository.getBookByTitle(request.getTitle().trim())).thenReturn(Optional.of(book));

      var exception = assertThrows(BadRequestDetailsException.class, () ->
          bookService.createBook(request, null));
      assertEquals("Invalid create book request", exception.getMessage());

      var violations = new HashMap<>();
      violations.put("title", "There is another book existed with the same title");
      assertEquals(violations, exception.getViolations());

      verify(bookRepository, times(1)).findFirstByOrderById();
      verify(bookRepository, times(1)).getCurrentBookIdSequenceValue();
      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookTypeService, times(1)).getBookTypeByString(anyString(), anyBoolean());
      verify(bookFieldService, times(1)).getBookFieldByString(anyString(), anyBoolean());
      verify(bookRepository, never()).save(any());
    }

    log.info("Passed - Create book when book with same title existed");
  }

  @Test
  public void testUpdateBookHappyFlow() {
    log.info("Test running - Update book happy flow...");

    var request = UpdateBookDto.builder()
        .title("Book Title 2")
        .authors("Another Author")
        .publisher("Another Publisher")
        .type("Thesis")
        .field("Another Field")
        .year(2024)
        .edition(2)
        .isbn("123-4-567890-12-4")
        .coverUrl(null)
        .quantity(2)
        .build();

    BookType bt = BookType.builder()
        .name("Thesis")
        .build();

    BookField bf = BookField.builder()
        .name("Another Field")
        .build();

    book.setId(100L);

    var mockCoverImageFile = new MockMultipartFile("123-4-567890-12-3.jpg", "mockCover.jpg",
        "image/jpeg", "This is a mock cover".getBytes());

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(bookRepository.getBookByTitle(request.getTitle().trim())).thenReturn(Optional.empty());
      when(bookRepository.getBookById(book.getId())).thenReturn(Optional.of(book));
      when(bookTypeService.getBookTypeByString(request.getType().trim(), false))
          .thenReturn(bt);
      when(bookFieldService.getBookFieldByString(request.getField().trim(), false))
          .thenReturn(bf);

      bookService.updateBook(book.getId(), request, mockCoverImageFile);

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookTypeService, times(1)).getBookTypeByString(request.getType().trim(),
          false);
      verify(bookFieldService, times(1)).getBookFieldByString(request.getField().trim(),
          false);
      verify(bookRepository, times(1)).getBookByTitle(request.getTitle().trim());
      verify(bookRepository, times(1)).getBookById(book.getId());
      verify(borrowEntryRepository, times(1)).countOpenedBorrowEntriesByBook(book);
      verify(bookRepository, times(1)).updateBookById(
          book.getId(),
          request.getTitle().trim(),
          request.getAuthors().trim(),
          request.getPublisher().trim(),
          bookTypeService.getBookTypeByString(request.getType().trim(), false),
          bookFieldService.getBookFieldByString(request.getField().trim(), false),
          request.getYear(),
          request.getEdition(),
          request.getIsbn().trim(),
          bookService.generateInventoryNumber(book.getId(), book.getType().getName(), book.getYear()),
          request.getQuantity(),
          bookService.getCoverImagePath(request.getCoverUrl(), mockCoverImageFile, book.getId())
      );
    }

    log.info("Passed - Update book happy flow");
  }

  @Test
  public void testUpdateBookInputsInvalidFlow() {
    log.info("Test running - Update book when inputs are invalid...");

    var request = UpdateBookDto.builder()
        .title("Book Title 2")
        .authors("Another Author")
        .publisher("Another Publisher")
        .type("Thesis")
        .field("Another Field")
        .year(2024)
        .edition(2)
        .isbn("123-4-567890-12-4")
        .coverUrl(null)
        .quantity(2)
        .build();

    book.setId(100L);

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      Map<String, String> violations = new HashMap<>();
      violations.put("title", "Book title must have at least 1 character and at most 100 characters");
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(violations);

      var mockCoverImageFile = new MockMultipartFile("123-4-567890-12-3.jpg", "mockCover.jpg",
          "image/jpeg", "This is a mock cover".getBytes());
      var exception = assertThrows(BadRequestDetailsException.class,
          () -> bookService.updateBook(book.getId(), request, mockCoverImageFile));
      assertEquals(exception.getMessage(), "Invalid create book request");
      assertEquals(exception.getViolations(), violations);

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, never()).getBookByTitle(request.getTitle().trim());
      verify(bookRepository, never()).getBookById(book.getId());
      verify(borrowEntryRepository, never()).countOpenedBorrowEntriesByBook(book);
      verify(bookTypeService, never()).getBookTypeByString(request.getType().trim(),
          false);
      verify(bookFieldService, never()).getBookFieldByString(request.getField().trim(),
          false);
      verify(bookRepository, never()).updateBookById(any(), any(), any(), any(), any(), any(), any(), any(), any(),
          any(), any(), any());
    }

    log.info("Passed - Update book when inputs are invalid");
  }

  @Test
  public void testUpdateBookBookWithSameTitleExistedFlow() {
    log.info("Test running - Update book when another book with same title already existed...");

    var request = UpdateBookDto.builder()
        .title("Book Title 2")
        .authors("Another Author")
        .publisher("Another Publisher")
        .type("Thesis")
        .field("Another Field")
        .year(2024)
        .edition(2)
        .isbn("123-4-567890-12-4")
        .coverUrl(null)
        .quantity(2)
        .build();

    book.setId(100L);

    Book book2 = Book.builder()
        .id(101L)
        .title("Book Title 2")
        .authors("Author")
        .publisher("Book")
        .type(BookType.builder().name("Book").build())
        .field(BookField.builder().name("Field").build())
        .year(2023)
        .edition(1)
        .isbn("123-4-567890-12-5")
        .inventoryNumber("101 CSST BO 2023")
        .coverImage(null)
        .quantity(1)
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(bookRepository.getBookByTitle(request.getTitle().trim())).thenReturn(Optional.of(book2));

      var mockCoverImageFile = new MockMultipartFile("123-4-567890-12-3.jpg", "mockCover.jpg",
          "image/jpeg", "This is a mock cover".getBytes());
      var exception = assertThrows(BadRequestDetailsException.class,
          () -> bookService.updateBook(book.getId(), request, mockCoverImageFile));
      assertEquals(exception.getMessage(), "Invalid create book request");
      Map<String, String> violations = new HashMap<>();
      violations.put("title", "A book with the same title is already stored in the library");
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, times(1)).getBookByTitle(request.getTitle().trim());
      verify(bookRepository, never()).getBookById(book.getId());
      verify(borrowEntryRepository, never()).countOpenedBorrowEntriesByBook(book);
      verify(bookTypeService, never()).getBookTypeByString(request.getType().trim(),
          false);
      verify(bookFieldService, never()).getBookFieldByString(request.getField().trim(),
          false);
      verify(bookRepository, never()).updateBookById(any(), any(), any(), any(), any(), any(), any(), any(), any(),
          any(), any(), any());
    }

    log.info("Passed - Update book when another book with same title already existed");
  }

  @Test
  public void testUpdateBookBookNotFoundFlow() {
    log.info("Test running - Update book when book is not exists...");

    book.setId(100L);

    var request = UpdateBookDto.builder()
        .title("Book Title 2")
        .authors("Another Author")
        .publisher("Another Publisher")
        .type("Thesis")
        .field("Another Field")
        .year(2024)
        .edition(2)
        .isbn("123-4-567890-12-4")
        .coverUrl(null)
        .quantity(2)
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(bookRepository.getBookByTitle(request.getTitle().trim())).thenReturn(Optional.of(book));
      when(bookRepository.getBookById(100L)).thenReturn(Optional.empty());

      var mockCoverImageFile = new MockMultipartFile("123-4-567890-12-3.jpg", "mockCover.jpg",
          "image/jpeg", "This is a mock cover".getBytes());
      var exception = assertThrows(BadRequestDetailsException.class,
          () -> bookService.updateBook(100L, request, mockCoverImageFile));
      assertEquals(exception.getMessage(), "Invalid create book request");
      Map<String, String> violations = new HashMap<>();
      violations.put("id", "There is no book with id 100");
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, times(1)).getBookByTitle(request.getTitle().trim());
      verify(bookRepository, times(1)).getBookById(book.getId());
      verify(borrowEntryRepository, never()).countOpenedBorrowEntriesByBook(book);
      verify(bookTypeService, never()).getBookTypeByString(request.getType().trim(),
          false);
      verify(bookFieldService, never()).getBookFieldByString(request.getField().trim(),
          false);
      verify(bookRepository, never()).updateBookById(any(), any(), any(), any(), any(), any(), any(), any(), any(),
          any(), any(), any());
    }

    log.info("Passed - Update book when book is not exists");
  }

  @Test
  public void testUpdateBookUnableToUpdateQuantityFlow() {
    log.info("Test running - Update book when new quantity is invalid...");

    var request = UpdateBookDto.builder()
        .title("Book Title 2")
        .authors("Another Author")
        .publisher("Another Publisher")
        .type("Thesis")
        .field("Another Field")
        .year(2024)
        .edition(2)
        .isbn("123-4-567890-12-4")
        .coverUrl(null)
        .quantity(0)
        .build();

    book.setId(100L);

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(bookRepository.getBookByTitle(request.getTitle().trim())).thenReturn(Optional.empty());
      when(bookRepository.getBookById(book.getId())).thenReturn(Optional.of(book));
      when(borrowEntryRepository.countOpenedBorrowEntriesByBook(book)).thenReturn(1);

      var mockCoverImageFile = new MockMultipartFile("123-4-567890-12-3.jpg", "mockCover.jpg",
          "image/jpeg", "This is a mock cover".getBytes());
      var exception = assertThrows(BadRequestDetailsException.class,
          () -> bookService.updateBook(book.getId(), request, mockCoverImageFile));
      assertEquals(exception.getMessage(), "Invalid create book request");
      Map<String, String> violations = new HashMap<>();
      violations.put("quantity", "New quantity of a book can't be smaller than number of issued books");
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(bookRepository, times(1)).getBookByTitle(request.getTitle().trim());
      verify(bookRepository, times(1)).getBookById(book.getId());
      verify(borrowEntryRepository, times(1)).countOpenedBorrowEntriesByBook(book);
      verify(bookTypeService, never()).getBookTypeByString(request.getType().trim(),
          false);
      verify(bookFieldService, never()).getBookFieldByString(request.getField().trim(),
          false);
      verify(bookRepository, never()).updateBookById(any(), any(), any(), any(), any(), any(), any(), any(), any(),
          any(), any(), any());
    }

    log.info("Passed - Update book when new quantity is invalid");
  }

  @Test
  public void testDeleteBookHappyFlow() throws IOException {
    log.info("Test running - Delete book happy flow...");

    book.setId(100L);
    book.setCoverImage("./100.jpg");
    assert book.getCoverImage() != null;
    File coverImage = new File(book.getCoverImage());
    if (!coverImage.createNewFile()) {
      fail("Can't create mock cover image file for the test");
    }

    when(borrowEntryRepository.countClosedBorrowEntries(book)).thenReturn(0);
    when(borrowEntryRepository.countOpenedBorrowEntriesByBook(book)).thenReturn(0);
    when(bookRepository.getBookById(book.getId())).thenReturn(Optional.of(book));

    bookService.deleteBookById(book.getId());

    verify(borrowEntryRepository, times(1)).countClosedBorrowEntries(book);
    verify(borrowEntryRepository, times(1)).countOpenedBorrowEntriesByBook(book);
    verify(bookRepository, times(1)).deleteBookById(book.getId());

    log.info("Passed - Delete book happy flow");
  }

  @Test
  public void testDeleteBookBorrowEntriesExistsFlow() {
    log.info("Test running - Delete book when there are borrow entries of the book...");

    book.setId(100L);

    when(borrowEntryRepository.countClosedBorrowEntries(book)).thenReturn(1);
    when(borrowEntryRepository.countOpenedBorrowEntriesByBook(book)).thenReturn(0);
    when(bookRepository.getBookById(book.getId())).thenReturn(Optional.of(book));

    var exception = assertThrows(BadRequestDetailsException.class, () -> bookService.deleteBookById(book.getId()));
    assertEquals("Unable to delete book", exception.getMessage());
    Map<String, String> violations = new HashMap<>();
    violations.put("book", "The book with id " + book.getId() + " has one or more borrow entries connected to it");
    assertEquals(violations, exception.getViolations());

    verify(borrowEntryRepository, times(1)).countClosedBorrowEntries(book);
    verify(borrowEntryRepository, times(1)).countOpenedBorrowEntriesByBook(book);
    verify(bookRepository, never()).deleteBookById(book.getId());

    log.info("Passed - Delete book when there are borrow entries of the book");
  }
}
