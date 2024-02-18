package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.*;
import inst.iop.LibraryManager.library.entities.Book;
import inst.iop.LibraryManager.library.services.BookService;
import inst.iop.LibraryManager.utilities.responses.ApiResponseEntityFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@CrossOrigin
@RestController
public class BookControllerImpl implements BookController {

  private final BookService bookService;
  private final ApiResponseEntityFactory responseEntityFactory;

  /**
   * The API end-point for listing all books in the library with pagination
   *
   * @param  pageNumber page number
   * @param  pageSize number of entries in a page
   * @return ResponseEntity that contains a message, http response code - 200 in success and 400 in error case, and
   * a list of books in success case or violations from input in error case
   */
  @Override
  public ResponseEntity<Object> listAllBooks(Integer pageNumber, Integer pageSize) {
    Page<Book> books = bookService.listAllBooks(new ListAllBooksDto(pageNumber, pageSize));
    return findBooksResultConstructor(books);
  }

  /**
   * The API end-point for getting a book by id
   *
   * @param  id book's id
   * @return ResponseEntity that contains a message, http response code - 200 in success and 400 in error case, and
   * a list of books in success case or violations from input in error case
   */
  @Override
  public ResponseEntity<Object> getBookById(Long id) {
    Book book = bookService.findBookById(id);
    Map<String, Object> details = new HashMap<>();
    details.put("book", book);
    return responseEntityFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully get book details", details
    );
  }

  /**
   * The API end-point for searching book in every category using a search term.
   *
   * @param  term search term
   * @param  beforeYear upper limit of book's published year
   * @param  afterYear lower limit of book's published year
   * @param  pageNumber page index number
   * @param  pageSize number of entries in a page
   * @return ResponseEntity that contains a message, http response code - 200 in success and 400 in error case, and
   * a list of books in success case or violations from input in error case
   */
  @Override
  public ResponseEntity<Object> findBooks(String term, Integer beforeYear, Integer afterYear, Integer pageNumber,
                                          Integer pageSize) {
    Page<Book> books = bookService.findBooks(new SearchBooksDto(term, beforeYear, afterYear, pageNumber, pageSize));
    return findBooksResultConstructor(books);
  }

  /**
   * The API end-point for searching book base on title using a search term.
   *
   * @param  term search term
   * @param  beforeYear upper limit of book's published year
   * @param  afterYear lower limit of book's published year
   * @param  pageNumber page index number
   * @param  pageSize number of entries in a page
   * @return ResponseEntity that contains a message, http response code - 200 in success and 400 in error case, and
   * a list of books in success case or violations from input in error case
   */
  @Override
  public ResponseEntity<Object> findBooksByTitle(String term, Integer beforeYear, Integer afterYear, Integer pageNumber,
                                                 Integer pageSize) {
    Page<Book> books = bookService.findBooksByTitle(
        new SearchBooksDto(term, beforeYear, afterYear, pageNumber, pageSize)
    );
    return findBooksResultConstructor(books);
  }

  /**
   * The API end-point for searching book base on authors using a search term.
   *
   * @param  term search term
   * @param  beforeYear upper limit of book's published year
   * @param  afterYear lower limit of book's published year
   * @param  pageNumber page index number
   * @param  pageSize number of entries in a page
   * @return ResponseEntity that contains a message, http response code - 200 in success and 400 in error case, and
   * a list of books in success case or violations from input in error case
   */
  @Override
  public ResponseEntity<Object> findBooksByAuthors(String term, Integer beforeYear, Integer afterYear,
                                                   Integer pageNumber, Integer pageSize) {
    Page<Book> books = bookService.findBooksByAuthors(
        new SearchBooksDto(term, beforeYear, afterYear, pageNumber, pageSize)
    );
    return findBooksResultConstructor(books);
  }

  /**
   * The API end-point for searching book base on publisher using a search term.
   *
   * @param  term search term
   * @param  beforeYear upper limit of book's published year
   * @param  afterYear lower limit of book's published year
   * @param  pageNumber page index number
   * @param  pageSize number of entries in a page
   * @return ResponseEntity that contains a message, http response code - 200 in success and 400 in error case, and
   * a list of books in success case or violations from input in error case
   */
  @Override
  public ResponseEntity<Object> findBooksByPublisher(String term, Integer beforeYear, Integer afterYear,
                                                     Integer pageNumber, Integer pageSize) {
    Page<Book> books = bookService.findBooksByPublisher(
        new SearchBooksDto(term, beforeYear, afterYear, pageNumber, pageSize)
    );
    return findBooksResultConstructor(books);
  }

  /**
   * The API end-point for searching book base on book type using a search term.
   *
   * @param  term search term
   * @param  beforeYear upper limit of book's published year
   * @param  afterYear lower limit of book's published year
   * @param  pageNumber page index number
   * @param  pageSize number of entries in a page
   * @return ResponseEntity that contains a message, http response code - 200 in success and 400 in error case, and
   * a list of books in success case or violations from input in error case
   */
  @Override
  public ResponseEntity<Object> findBooksByType(String term, Integer beforeYear, Integer afterYear,
                                                Integer pageNumber, Integer pageSize) {
    Page<Book> books = bookService.findBooksByType(
        new SearchBooksDto(term, beforeYear, afterYear, pageNumber, pageSize)
    );
    return findBooksResultConstructor(books);
  }

  /**
   * The API end-point for searching book base on book field using a search term.
   *
   * @param  term search term
   * @param  beforeYear upper limit of book's published year
   * @param  afterYear lower limit of book's published year
   * @param  pageNumber page index number
   * @param  pageSize number of entries in a page
   * @return ResponseEntity that contains a message, http response code - 200 in success and 400 in error case, and
   * a list of books in success case or violations from input in error case
   */
  @Override
  public ResponseEntity<Object> findBooksByField(String term, Integer beforeYear, Integer afterYear,
                                                 Integer pageNumber, Integer pageSize) {
    Page<Book> books = bookService.findBooksByField(
        new SearchBooksDto(term, beforeYear, afterYear, pageNumber, pageSize)
    );
    return findBooksResultConstructor(books);
  }

  /**
   * The API end-point for searching book base on ISBN using a search term.
   *
   * @param  term search term
   * @param  beforeYear upper limit of book's published year
   * @param  afterYear lower limit of book's published year
   * @param  pageNumber page index number
   * @param  pageSize number of entries in a page
   * @return ResponseEntity that contains a message, http response code - 200 in success and 400 in error case, and
   * a list of books in success case or violations from input in error case
   */
  @Override
  public ResponseEntity<Object> findBooksByIsbn(String term, Integer beforeYear, Integer afterYear,
                                                Integer pageNumber, Integer pageSize) {
    Page<Book> books = bookService.findBooksByIsbn(
        new SearchBooksDto(term, beforeYear, afterYear, pageNumber, pageSize)
    );
    return findBooksResultConstructor(books);
  }

  /**
   * The API end-point for searching book base on inventory number using a search term.
   *
   * @param  term search term
   * @param  beforeYear upper limit of book's published year
   * @param  afterYear lower limit of book's published year
   * @param  pageNumber page index number
   * @param  pageSize number of entries in a page
   * @return ResponseEntity that contains a message, http response code - 200 in success and 400 in error case, and
   * a list of books in success case or violations from input in error case
   */
  @Override
  public ResponseEntity<Object> findBooksByInventoryNumber(String term, Integer beforeYear, Integer afterYear,
                                                           Integer pageNumber, Integer pageSize) {
    Page<Book> books = bookService.findBooksByInventoryNumber(
        new SearchBooksDto(term, beforeYear, afterYear, pageNumber, pageSize)
    );
    return findBooksResultConstructor(books);
  }

  /**
   * The API end-point exposes a book's cover image if it is not null
   *
   * @param  id book's id
   * @return ResponseEntity that contains a message, http response code - 200 in success and 400 in error case, and
   * a list of books in success case or violations from input in error case
   */
  @Override
  public ResponseEntity<?> getCoverImage(Long id) {
    String coverImagePath = bookService.getCoverImagePath(id);
    if (coverImagePath == null) {
      return responseEntityFactory.createErrorResponse(
          HttpStatus.NO_CONTENT, "Book with id " + id + " has no content"
      );
    }

    Resource resource = new ClassPathResource(coverImagePath);
    return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
  }

  /**
   * Helper function to construct books' details to a proper response body
   *
   * @param borrowEntries a page of borrow entries
   * @return ResponseEntity that contains a report message and http response code 200
   */
  private ResponseEntity<Object> findBooksResultConstructor(Page<Book> books) {
    Map<String, Object> details = new HashMap<>();
    details.put("books", books.stream().sorted(Comparator.comparingLong(Book::getId)));
    details.put("pageNumber", books.getNumber());
    details.put("pageSize", books.getSize());
    details.put("numberOfPages", books.getTotalPages());
    return responseEntityFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully query books", details
    );
  }

  /**
   * The API end-point for creating a new book
   *
   * @param request    contains every book's details
   * @param coverImage for uploading image from local machine
   * @return ResponseEntity that contains a message, http response code - 200 in success and 400 in error case, and
   * a list of violations from inputs in error case
   */
  @Override
  @Transactional
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> createBook(CreateBookDto request, MultipartFile coverImage) {
    bookService.createBook(request, coverImage);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.ACCEPTED, "Successfully create book"
    );
  }

  /**
   * The API end-point for updating an existed books
   *
   * @param  request contains every book's details
   * @param  coverImage for uploading image from local machine
   * @return ResponseEntity that contains a message, http response code - 200 in success and 400 in error case, and
   * a list of violations from inputs in error case
   */
  @Override
  @Transactional
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> updateBookById(Long id, UpdateBookDto request, MultipartFile coverImage) {
    bookService.updateBook(id, request, coverImage);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.ACCEPTED, "Successfully update book"
    );
  }

  /**
   * The API end-point for deleting an existed books
   *
   * @param  id book's id
   * @return ResponseEntity that contains a message and http response code - 200 if success
   */
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
