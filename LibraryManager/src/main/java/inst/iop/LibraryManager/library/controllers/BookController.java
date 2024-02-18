package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.CreateBookDto;
import inst.iop.LibraryManager.library.dtos.UpdateBookDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/v1/books")
@Validated
public interface BookController {

  /**
   * The API end-point for listing all books in the library with pagination
   *
   * @param  pageNumber page number
   * @param  pageSize number of entries in a page
   * @return ResponseEntity that contains a message, http response code - 200 in success and 400 in error case, and
   * a list of books in success case or violations from input in error case
   */
  @GetMapping("")
  ResponseEntity<Object> listAllBooks(@RequestParam(defaultValue = "0") Integer pageNumber,
                                      @RequestParam(defaultValue = "10") Integer pageSize);

  /**
   * The API end-point for getting a book by id
   *
   * @param  id book's id
   * @return ResponseEntity that contains a message, http response code - 200 in success and 400 in error case, and
   * a list of books in success case or violations from input in error case
   */
  @GetMapping("/{id}")
  ResponseEntity<Object> getBookById(@PathVariable Long id);

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
  @GetMapping("/search")
  ResponseEntity<Object> findBooks(
      @RequestParam String term,
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @RequestParam(defaultValue = "10") Integer pageSize
  );

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
  @GetMapping("/search/title")
  ResponseEntity<Object> findBooksByTitle(
      @RequestParam String term,
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @RequestParam(defaultValue = "10") Integer pageSize
  );

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
  @GetMapping("/search/authors")
  ResponseEntity<Object> findBooksByAuthors(
      @RequestParam String term,
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @RequestParam(defaultValue = "10") Integer pageSize
  );

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
  @GetMapping("/search/publisher")
  ResponseEntity<Object> findBooksByPublisher(
      @RequestParam String term,
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @RequestParam(defaultValue = "10") Integer pageSize
  );

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
  @GetMapping("/search/type")
  ResponseEntity<Object> findBooksByType(
      @RequestParam String term,
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @RequestParam(defaultValue = "10") Integer pageSize
  );

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
  @GetMapping("/search/field")
  ResponseEntity<Object> findBooksByField(
      @RequestParam String term,
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @RequestParam(defaultValue = "10") Integer pageSize
  );

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
  @GetMapping("/search/isbn")
  ResponseEntity<Object> findBooksByIsbn(
      @RequestParam String term,
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @RequestParam(defaultValue = "10") Integer pageSize
  );

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
  @GetMapping("/search/inventory-number")
  ResponseEntity<Object> findBooksByInventoryNumber(
      @RequestParam String term,
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @RequestParam(defaultValue = "10") Integer pageSize
  );

  /**
   * The API end-point exposes a book's cover image
   *
   * @param  id book's id
   * @return ResponseEntity that contains a message, http response code - 200 in success and 400 in error case, and
   * a list of books in success case or violations from input in error case
   */
  @GetMapping("/{id}/cover")
  ResponseEntity<?> getCoverImage(@PathVariable Long id);

  /**
   * The API end-point for creating a new book
   *
   * @param request    contains every book's details
   * @param coverImage for uploading image from local machine
   * @return ResponseEntity that contains a message, http response code - 200 in success and 400 in error case, and
   * a list of violations from inputs in error case
   */
  @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  ResponseEntity<Object> createBook(@ModelAttribute CreateBookDto request,
                                    @RequestPart(name = "coverImage", required = false) MultipartFile coverImage);

  /**
   * The API end-point for updating an existed books
   *
   * @param  request contains every book's details
   * @param  coverImage for uploading image from local machine
   * @return ResponseEntity that contains a message, http response code - 200 in success and 400 in error case, and
   * a list of violations from inputs in error case
   */
  @PostMapping(value = "/update/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  ResponseEntity<Object> updateBookById(@PathVariable Long id, @ModelAttribute UpdateBookDto request,
                                        @RequestPart(name = "coverImage", required = false) MultipartFile coverImage);

  /**
   * The API end-point for deleting an existed books
   *
   * @param  id book's id
   * @return ResponseEntity that contains a message and http response code - 200 if success
   */
  @DeleteMapping("/delete/{id}")
  ResponseEntity<Object> deleteBookById(@PathVariable Long id);
}
