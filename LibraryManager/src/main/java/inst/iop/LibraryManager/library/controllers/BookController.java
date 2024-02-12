package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.CreateBookDto;
import inst.iop.LibraryManager.library.dtos.ListAllBooksDto;
import inst.iop.LibraryManager.library.dtos.SearchBooksDto;
import inst.iop.LibraryManager.library.dtos.UpdateBookDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/book")
public interface BookController {

  @GetMapping("/")
  ResponseEntity<Object> getAllBooks(@Valid ListAllBooksDto booksDto, BindingResult bindingResult);

  @GetMapping("/{id}")
  ResponseEntity<Object> getBookById(@PathVariable long id);

  @GetMapping("/search")
  ResponseEntity<Object> findBooks(@Valid SearchBooksDto booksDto, BindingResult bindingResult);

  @GetMapping("/search/title")
  ResponseEntity<Object> findBooksByTitle(@Valid SearchBooksDto booksDto, BindingResult bindingResult);

  @GetMapping("/search/authors")
  ResponseEntity<Object> findBooksByAuthors(@Valid SearchBooksDto booksDto, BindingResult bindingResult);

  @GetMapping("/search/publisher")
  ResponseEntity<Object> findBooksByPublisher(@Valid SearchBooksDto booksDto, BindingResult bindingResult);

  @GetMapping("/search/type")
  ResponseEntity<Object> findBooksByType(@Valid SearchBooksDto booksDto, BindingResult bindingResult);

  @GetMapping("/search/field")
  ResponseEntity<Object> findBooksByField(@Valid SearchBooksDto booksDto, BindingResult bindingResult);

  @GetMapping("/search/isbn")
  ResponseEntity<Object> findBooksByIsbn(@Valid SearchBooksDto booksDto, BindingResult bindingResult);

  @GetMapping("/search/inventory-number")
  ResponseEntity<Object> findBooksByInventoryNumber(@Valid SearchBooksDto booksDto, BindingResult bindingResult);

  @PostMapping("/create")
  ResponseEntity<Object> createBook(@RequestBody @Valid CreateBookDto bookDto, BindingResult bindingResult);

  @PostMapping("/update/{id}")
  ResponseEntity<Object> updateBookById(@PathVariable Long id, @RequestBody @Valid UpdateBookDto bookDto,
                                   BindingResult bindingResult);

  @DeleteMapping("/delete/{id}")
  ResponseEntity<Object> deleteBookById(@PathVariable Long id);
}
