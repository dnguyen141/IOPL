package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.CreateBookDto;
import inst.iop.LibraryManager.library.dtos.ListAllBooksDto;
import inst.iop.LibraryManager.library.dtos.UpdateBookDto;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/v1/books")
@Validated
public interface BookController {

  @GetMapping("")
  ResponseEntity<Object> listAllBooks(@RequestParam(defaultValue = "0") Integer pageNumber,
                                      @RequestParam(defaultValue = "10") Integer pageSize);

  @GetMapping("/{id}")
  ResponseEntity<Object> getBookById(@PathVariable Long id);

  @GetMapping("/search")
  ResponseEntity<Object> findBooks(
      @RequestParam String term,
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @RequestParam(defaultValue = "10") Integer pageSize
  );

  @GetMapping("/search/title")
  ResponseEntity<Object> findBooksByTitle(
      @RequestParam String term,
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @RequestParam(defaultValue = "10") Integer pageSize
  );

  @GetMapping("/search/authors")
  ResponseEntity<Object> findBooksByAuthors(
      @RequestParam String term,
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @RequestParam(defaultValue = "10") Integer pageSize
  );

  @GetMapping("/search/publisher")
  ResponseEntity<Object> findBooksByPublisher(
      @RequestParam String term,
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @RequestParam(defaultValue = "10") Integer pageSize
  );

  @GetMapping("/search/type")
  ResponseEntity<Object> findBooksByType(
      @RequestParam String term,
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @RequestParam(defaultValue = "10") Integer pageSize
  );

  @GetMapping("/search/field")
  ResponseEntity<Object> findBooksByField(
      @RequestParam String term,
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @RequestParam(defaultValue = "10") Integer pageSize
  );

  @GetMapping("/search/isbn")
  ResponseEntity<Object> findBooksByIsbn(
      @RequestParam String term,
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @RequestParam(defaultValue = "10") Integer pageSize
  );

  @GetMapping("/search/inventory-number")
  ResponseEntity<Object> findBooksByInventoryNumber(
      @RequestParam String term,
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @RequestParam(defaultValue = "10") Integer pageSize
  );

  @GetMapping("/{id}/cover")
  ResponseEntity<Resource> getCoverImage(@PathVariable Long id);

  @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  ResponseEntity<Object> createBook(@ModelAttribute CreateBookDto request,
                                    @RequestPart(name = "coverImage", required = false) MultipartFile coverFile);

  @PostMapping(value = "/update/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  ResponseEntity<Object> updateBookById(@PathVariable Long id, @ModelAttribute UpdateBookDto request,
                                        @RequestPart(name = "coverImage", required = false) MultipartFile coverImage);

  @DeleteMapping("/delete/{id}")
  ResponseEntity<Object> deleteBookById(@PathVariable Long id);
}
