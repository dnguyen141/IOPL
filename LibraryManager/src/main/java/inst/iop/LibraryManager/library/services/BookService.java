package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.library.dtos.CreateBookDto;
import inst.iop.LibraryManager.library.dtos.UpdateBookDto;
import inst.iop.LibraryManager.library.entities.Book;
import inst.iop.LibraryManager.library.entities.constrains.PageNumberConstrain;
import inst.iop.LibraryManager.library.entities.constrains.PageSizeConstrain;
import inst.iop.LibraryManager.library.entities.constrains.YearConstrain;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

public interface BookService {

  Page<Book> getAllBooks(Integer pageNumber, Integer pageSize);

  Optional<Book> getBookFromId(Long id);

  Optional<Book> getBookFromTitle(String title);

  Page<Book> findBooks(String title, String authors, String publisher, String type, String field, Integer beforeYear,
                       Integer afterYear, String isbn, String inventoryNumber, Integer pageNumber, Integer pageSize);

  Page<Book> findBooksByTitle(String title, Integer beforeYear, Integer afterYear, Integer pageNumber,
                              Integer pageSize);

  Page<Book> findBooksByAuthors(String authors, Integer beforeYear, Integer afterYear, Integer pageNumber,
                                Integer pageSize);

  Page<Book> findBooksByPublisher(String publisher, Integer beforeYear, Integer afterYear, Integer pageNumber,
                                  Integer pageSize);

  Page<Book> findBooksByType(String type, Integer beforeYear, Integer afterYear, Integer pageNumber, Integer pageSize);

  Page<Book> findBooksByField(String field, Integer beforeYear, Integer afterYear, Integer pageNumber,
                              Integer pageSize);

  Page<Book> findBooksByIsbn(String isbn, Integer beforeYear, Integer afterYear, Integer pageNumber, Integer pageSize);

  Page<Book> findBooksByInventoryNumber(String inventoryNumber, Integer beforeYear, Integer afterYear,
                                        Integer pageNumber, Integer pageSize);

  void createBook(CreateBookDto bookDto) throws IllegalArgumentException;

  void updateBook(Long id, UpdateBookDto bookDto) throws IllegalArgumentException;

  void deleteBookById(Long id);
}
