package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.*;
import inst.iop.LibraryManager.library.entities.Book;
import inst.iop.LibraryManager.library.services.BookService;
import inst.iop.LibraryManager.utilities.responses.ApiResponseEntityFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@CrossOrigin
@RestController
public class BookControllerImpl implements BookController {

  private final BookService bookService;
  private final ApiResponseEntityFactory responseEntityFactory;

  @Override
  public ResponseEntity<Object> listAllBooks(ListAllBooksDto request) {
    Page<Book> books = bookService.listAllBooks(request);
    return findBooksResultConstructor(books);
  }

  @Override
  public ResponseEntity<Object> getBookById(Long id) {
    Book book = bookService.findBookById(id);
    Map<String, Object> details = new HashMap<>();
    details.put("book", book);
    return responseEntityFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully get book details", details
    );
  }

  @Override
  public ResponseEntity<Object> findBooks(String term, Integer beforeYear, Integer afterYear, Integer pageNumber,
                                          Integer pageSize) {
    Page<Book> books = bookService.findBooks(new SearchBooksDto(term, beforeYear, afterYear, pageNumber, pageSize));
    return findBooksResultConstructor(books);
  }

  @Override
  public ResponseEntity<Object> findBooksByTitle(String term, Integer beforeYear, Integer afterYear, Integer pageNumber,
                                                 Integer pageSize) {
    Page<Book> books = bookService.findBooksByTitle(
        new SearchBooksDto(term, beforeYear, afterYear, pageNumber, pageSize)
    );
    return findBooksResultConstructor(books);
  }

  @Override
  public ResponseEntity<Object> findBooksByAuthors(String term, Integer beforeYear, Integer afterYear,
                                                   Integer pageNumber, Integer pageSize) {
    Page<Book> books = bookService.findBooksByAuthors(
        new SearchBooksDto(term, beforeYear, afterYear, pageNumber, pageSize)
    );
    return findBooksResultConstructor(books);
  }

  @Override
  public ResponseEntity<Object> findBooksByPublisher(String term, Integer beforeYear, Integer afterYear,
                                                     Integer pageNumber, Integer pageSize) {
    Page<Book> books = bookService.findBooksByPublisher(
        new SearchBooksDto(term, beforeYear, afterYear, pageNumber, pageSize)
    );
    return findBooksResultConstructor(books);
  }

  @Override
  public ResponseEntity<Object> findBooksByType(String term, Integer beforeYear, Integer afterYear,
                                                Integer pageNumber, Integer pageSize) {
    Page<Book> books = bookService.findBooksByType(
        new SearchBooksDto(term, beforeYear, afterYear, pageNumber, pageSize)
    );
    return findBooksResultConstructor(books);
  }

  @Override
  public ResponseEntity<Object> findBooksByField(String term, Integer beforeYear, Integer afterYear,
                                                 Integer pageNumber, Integer pageSize) {
    Page<Book> books = bookService.findBooksByField(
        new SearchBooksDto(term, beforeYear, afterYear, pageNumber, pageSize)
    );
    return findBooksResultConstructor(books);
  }

  @Override
  public ResponseEntity<Object> findBooksByIsbn(String term, Integer beforeYear, Integer afterYear,
                                                Integer pageNumber, Integer pageSize) {
    Page<Book> books = bookService.findBooksByIsbn(
        new SearchBooksDto(term, beforeYear, afterYear, pageNumber, pageSize)
    );
    return findBooksResultConstructor(books);
  }

  @Override
  public ResponseEntity<Object> findBooksByInventoryNumber(String term, Integer beforeYear, Integer afterYear,
                                                           Integer pageNumber, Integer pageSize) {
    Page<Book> books = bookService.findBooksByInventoryNumber(
        new SearchBooksDto(term, beforeYear, afterYear, pageNumber, pageSize)
    );
    return findBooksResultConstructor(books);
  }

  @Override
  public ResponseEntity<Object> getCoverImage(Long id) {
    CoverImageDto coverImageDto = bookService.getCoverImage(id);
    return ResponseEntity.ok().body(coverImageDto);
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
  public ResponseEntity<Object> createBook(CreateBookDto request, MultipartFile coverImage) {
    bookService.createBook(request, coverImage);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.ACCEPTED, "Successfully create book"
    );
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> updateBookById(Long id, UpdateBookDto request, MultipartFile coverImage) {
    bookService.updateBook(id, request, coverImage);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.ACCEPTED, "Successfully update book"
    );
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> deleteBookById(Long id) {
    bookService.deleteBookById(id);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.OK, "Successfully delete book"
    );
  }
}
