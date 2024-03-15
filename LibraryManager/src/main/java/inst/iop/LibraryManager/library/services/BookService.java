package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.library.dtos.*;
import inst.iop.LibraryManager.library.entities.Book;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface BookService {

  Page<Book> listAllBooks(ListAllBooksDto request);

  Book findBookById(Long id) throws BadRequestDetailsException;

  Page<Book> findBooks(SearchBooksDto request) throws BadRequestDetailsException;

  Page<Book> findBooksByTitle(SearchBooksDto request) throws BadRequestDetailsException;

  Page<Book> findBooksByAuthors(SearchBooksDto request) throws BadRequestDetailsException;

  Page<Book> findBooksByPublisher(SearchBooksDto request) throws BadRequestDetailsException;

  Page<Book> findBooksByType(SearchBooksDto request) throws BadRequestDetailsException;

  Page<Book> findBooksByField(SearchBooksDto request) throws BadRequestDetailsException;

  Page<Book> findBooksByIsbn(SearchBooksDto request) throws BadRequestDetailsException;

  Page<Book> findBooksByInventoryNumber(SearchBooksDto request) throws BadRequestDetailsException;

  String getCoverImagePath(Long id);

  Book createBook(CreateBookDto request, MultipartFile coverImage) throws BadRequestDetailsException;

  void updateBook(Long id, UpdateBookDto bookDto, MultipartFile coverImage) throws BadRequestDetailsException;

  void deleteBookById(Long id) throws BadRequestDetailsException;

  void importBooksFromExcelFile(MultipartFile excelFile) throws BadRequestDetailsException;
}
