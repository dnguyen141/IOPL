package inst.iop.LibraryManager.library.controllers;


import inst.iop.LibraryManager.library.dtos.CreateBookDto;
import inst.iop.LibraryManager.library.dtos.ListAllBooksDto;
import inst.iop.LibraryManager.library.dtos.SearchBooksDto;
import inst.iop.LibraryManager.library.dtos.UpdateBookDto;
import inst.iop.LibraryManager.library.entities.Book;
import inst.iop.LibraryManager.utilities.responses.ApiResponseEntityFactory;
import inst.iop.LibraryManager.library.services.BookServiceImpl;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static inst.iop.LibraryManager.utilities.BindingResultHandler.handleBindingResult;

@RestController
@RequiredArgsConstructor
public class BookControllerImpl implements BookController {

  private final BookServiceImpl bookService;
  private final ApiResponseEntityFactory responseEntityFactory;

  @Override
  public ResponseEntity<Object> getAllBooks(@Valid ListAllBooksDto booksDto, BindingResult bindingResult) {
    Map<String, Object> violations = handleBindingResult(bindingResult);
    if (!violations.isEmpty()) {
      return responseEntityFactory.createErrorWithDetailsResponse(
          HttpStatus.BAD_REQUEST, "Invalid book list request", violations
      );
    }

    Page<Book> books = bookService.getAllBooks(booksDto.getPageNumber(), booksDto.getPageSize());
    return findBooksResultConstructor(books);
  }

  @Override
  public ResponseEntity<Object> getBookById(@PathVariable long id) {
    Optional<Book> book = bookService.getBookFromId(id);
    if (book.isPresent()) {
      Map<String, Object> details = new HashMap<>();
      details.put("book", book.get());
      return responseEntityFactory.createSuccessWithDataResponse(
          HttpStatus.OK, "Successfully get book details", details
      );
    }

    return responseEntityFactory.createErrorResponse(
        HttpStatus.NO_CONTENT, "Book with id " + id + " is not existed"
    );
  }

  @Override
  public ResponseEntity<Object> findBooks(@Valid SearchBooksDto booksDto, BindingResult bindingResult) {
    Map<String, Object> violations = handleBindingResult(bindingResult);

    if (!violations.isEmpty()) {
      return responseEntityFactory.createErrorWithDetailsResponse(
          HttpStatus.BAD_REQUEST, "Invalid book search request", violations
      );
    }

    String term = booksDto.getTerm();
    Page<Book> books = bookService.findBooks(
        term, term, term, term, term, booksDto.getBeforeYear(), booksDto.getAfterYear(), term, term,
        booksDto.getPageNumber(), booksDto.getPageSize()
    );
    return findBooksResultConstructor(books);
  }

  @Override
  public ResponseEntity<Object> findBooksByTitle(@Valid SearchBooksDto booksDto, BindingResult bindingResult) {
    Map<String, Object> violations = handleBindingResult(bindingResult);

    if (!violations.isEmpty()) {
      return responseEntityFactory.createErrorWithDetailsResponse(
          HttpStatus.BAD_REQUEST, "Invalid book search by title request", violations
      );
    }

    Page<Book> books = bookService.findBooksByTitle(
        booksDto.getTerm(), booksDto.getBeforeYear(), booksDto.getAfterYear(), booksDto.getPageNumber(),
        booksDto.getPageSize()
    );
    return findBooksResultConstructor(books);
  }

  @Override
  public ResponseEntity<Object> findBooksByAuthors(@Valid SearchBooksDto booksDto, BindingResult bindingResult) {
    Map<String, Object> violations = handleBindingResult(bindingResult);

    if (!violations.isEmpty()) {
      return responseEntityFactory.createErrorWithDetailsResponse(
          HttpStatus.BAD_REQUEST, "Invalid book search by authors request", violations
      );
    }

    Page<Book> books = bookService.findBooksByAuthors(
        booksDto.getTerm(), booksDto.getBeforeYear(), booksDto.getAfterYear(), booksDto.getPageNumber(),
        booksDto.getPageSize()
    );
    return findBooksResultConstructor(books);
  }

  @Override
  public ResponseEntity<Object> findBooksByPublisher(@Valid SearchBooksDto booksDto, BindingResult bindingResult) {
    Map<String, Object> violations = handleBindingResult(bindingResult);

    if (!violations.isEmpty()) {
      return responseEntityFactory.createErrorWithDetailsResponse(
          HttpStatus.BAD_REQUEST, "Invalid book search by publisher request", violations
      );
    }

    Page<Book> books = bookService.findBooksByPublisher(
        booksDto.getTerm(), booksDto.getBeforeYear(), booksDto.getAfterYear(), booksDto.getPageNumber(),
        booksDto.getPageSize()
    );
    return findBooksResultConstructor(books);
  }

  @Override
  public ResponseEntity<Object> findBooksByType(@Valid SearchBooksDto booksDto, BindingResult bindingResult) {
    Map<String, Object> violations = handleBindingResult(bindingResult);

    if (!violations.isEmpty()) {
      return responseEntityFactory.createErrorWithDetailsResponse(
          HttpStatus.BAD_REQUEST, "Invalid book search by type request", violations
      );
    }

    Page<Book> books = bookService.findBooksByType(
        booksDto.getTerm(), booksDto.getBeforeYear(), booksDto.getAfterYear(), booksDto.getPageNumber(),
        booksDto.getPageSize()
    );
    return findBooksResultConstructor(books);
  }

  @Override
  public ResponseEntity<Object> findBooksByField(@Valid SearchBooksDto booksDto, BindingResult bindingResult) {
    Map<String, Object> violations = handleBindingResult(bindingResult);

    if (!violations.isEmpty()) {
      return responseEntityFactory.createErrorWithDetailsResponse(
          HttpStatus.BAD_REQUEST, "Invalid book search by field request", violations
      );
    }

    Page<Book> books = bookService.findBooksByField(
        booksDto.getTerm(), booksDto.getBeforeYear(), booksDto.getAfterYear(), booksDto.getPageNumber(),
        booksDto.getPageSize()
    );
    return findBooksResultConstructor(books);
  }

  @Override
  public ResponseEntity<Object> findBooksByIsbn(@Valid SearchBooksDto booksDto, BindingResult bindingResult) {
    Map<String, Object> violations = handleBindingResult(bindingResult);

    if (!violations.isEmpty()) {
      return responseEntityFactory.createErrorWithDetailsResponse(
          HttpStatus.BAD_REQUEST, "Invalid book search by field request", violations
      );
    }

    Page<Book> books = bookService.findBooksByIsbn(
        booksDto.getTerm(), booksDto.getBeforeYear(), booksDto.getAfterYear(), booksDto.getPageNumber(),
        booksDto.getPageSize()
    );
    return findBooksResultConstructor(books);
  }

  @Override
  public ResponseEntity<Object> findBooksByInventoryNumber(@Valid SearchBooksDto booksDto, BindingResult bindingResult) {
    Map<String, Object> violations = handleBindingResult(bindingResult);

    if (!violations.isEmpty()) {
      return responseEntityFactory.createErrorWithDetailsResponse(
          HttpStatus.BAD_REQUEST, "Invalid book search by inventory number request", violations
      );
    }

    Page<Book> books = bookService.findBooksByInventoryNumber(
        booksDto.getTerm(), booksDto.getBeforeYear(), booksDto.getAfterYear(), booksDto.getPageNumber(),
        booksDto.getPageSize()
    );
    return findBooksResultConstructor(books);
  }

  private ResponseEntity<Object> findBooksResultConstructor(Page<Book> books) {
    Map<String, Object> details = new HashMap<>();
    details.put("books", books.getContent().stream());
    details.put("pageNumber", books.getNumber());
    details.put("pageSize", books.getSize());
    details.put("numberOfPages", books.getTotalPages());
    return responseEntityFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully query books", details
    );
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> createBook(@RequestBody @Valid CreateBookDto bookDto, BindingResult bindingResult) {
    Map<String, Object> violations = createUpdateBookViolations(bindingResult, bookDto.getTitle(), true);
    if (!violations.isEmpty()) {
      return responseEntityFactory.createErrorWithDetailsResponse(
          HttpStatus.BAD_REQUEST, "Invalid book create request", violations
      );
    }

    bookService.createBook(bookDto);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.ACCEPTED, "Successfully create book"
    );
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> updateBookById(@PathVariable Long id, @RequestBody @Valid UpdateBookDto bookDto,
                                          BindingResult bindingResult
  ) {
    Map<String, Object> violations = createUpdateBookViolations(bindingResult, bookDto.getTitle(), false);
    if (!violations.isEmpty()) {
      return responseEntityFactory.createErrorWithDetailsResponse(
          HttpStatus.BAD_REQUEST, "Invalid book update request", violations
      );
    }

    bookService.updateBook(id, bookDto);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.ACCEPTED, "Successfully update book"
    );
  }

  private Map<String, Object> createUpdateBookViolations(BindingResult bindingResult, String bookTitle,
                                                         boolean isCreateRequest) {
    Map<String, Object> violations = handleBindingResult(bindingResult);
    Optional<Book> b = bookService.getBookFromTitle(bookTitle);
    if (b.isPresent() && isCreateRequest) {
      violations.put("title", "A book with the same title is already stored in the library");
    }
    return violations;
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> deleteBookById(@PathVariable Long id) {
    Optional<Book> book = bookService.getBookFromId(id);
    if (book.isEmpty()) {
      return responseEntityFactory.createErrorResponse(
          HttpStatus.NOT_FOUND, "Book with id " + id + " is not exists"
      );
    }

    bookService.deleteBookById(id);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.OK, "Successfully delete book"
    );
  }
}
