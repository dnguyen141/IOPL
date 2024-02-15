package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.library.dtos.*;
import inst.iop.LibraryManager.library.entities.Book;
import inst.iop.LibraryManager.library.entities.BookField;
import inst.iop.LibraryManager.library.entities.BookType;
import inst.iop.LibraryManager.library.repositories.BookRepository;
import inst.iop.LibraryManager.library.repositories.BorrowEntryRepository;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static inst.iop.LibraryManager.utilities.ConstraintViolationSetHandler.convertConstrainViolationSetToMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepository;
  private final BorrowEntryRepository borrowEntryRepository;
  private final BookTypeService bookTypeService;
  private final BookFieldService bookFieldService;
  private final ImageFileService imageFileService;
  private final Validator validator;

  public Page<Book> listAllBooks(ListAllBooksDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertConstrainViolationSetToMap(validator.validate(request));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid list all books request", violations);
    }

    return bookRepository.listAllBooks(PageRequest.of(request.getPageNumber(), request.getPageSize()));
  }

  @Override
  public Book findBookById(Long id) throws BadRequestDetailsException {
    Optional<Book> book = bookRepository.getBookById(id);
    if (book.isEmpty()) {
      Map<String, String> violations = new HashMap<>();
      violations.put("id", "There is no book with id " + id);
      throw new BadRequestDetailsException("Unable to get book with id " + id, violations);
    }
    return book.get();
  }

  @Override
  public Page<Book> findBooks(SearchBooksDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertConstrainViolationSetToMap(validator.validate(request));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid search books by term request", violations);
    }

    String term = request.getTerm();
    return bookRepository.findBooks(term, term, term, term, term, request.getBeforeYear(), request.getAfterYear(), term,
        term, PageRequest.of(request.getPageNumber(), request.getPageSize()));
  }

  @Override
  public Page<Book> findBooksByTitle(SearchBooksDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertConstrainViolationSetToMap(validator.validate(request));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid search books by title request", violations);
    }

    return bookRepository.findBooksByTitle(
        request.getTerm(),
        request.getBeforeYear(),
        request.getAfterYear(),
        PageRequest.of(request.getPageNumber(), request.getPageSize())
    );
  }

  @Override
  public Page<Book> findBooksByAuthors(SearchBooksDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertConstrainViolationSetToMap(validator.validate(request));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid search books by authors request", violations);
    }

    return bookRepository.findBooksByAuthors(
        request.getTerm(),
        request.getBeforeYear(),
        request.getAfterYear(),
        PageRequest.of(request.getPageNumber(), request.getPageSize())
    );
  }

  @Override
  public Page<Book> findBooksByPublisher(SearchBooksDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertConstrainViolationSetToMap(validator.validate(request));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid search books by publisher request", violations);
    }

    return bookRepository.findBooksByPublisher(
        request.getTerm(),
        request.getBeforeYear(),
        request.getAfterYear(),
        PageRequest.of(request.getPageNumber(), request.getPageSize())
    );
  }

  @Override
  public Page<Book> findBooksByType(SearchBooksDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertConstrainViolationSetToMap(validator.validate(request));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid search books by type request", violations);
    }

    return bookRepository.findBooksByType(
        request.getTerm(),
        request.getBeforeYear(),
        request.getAfterYear(),
        PageRequest.of(request.getPageNumber(), request.getPageSize())
    );
  }

  @Override
  public Page<Book> findBooksByField(SearchBooksDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertConstrainViolationSetToMap(validator.validate(request));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid search books by field request", violations);
    }

    return bookRepository.findBooksByField(
        request.getTerm(),
        request.getBeforeYear(),
        request.getAfterYear(),
        PageRequest.of(request.getPageNumber(), request.getPageSize())
    );
  }

  @Override
  public Page<Book> findBooksByIsbn(SearchBooksDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertConstrainViolationSetToMap(validator.validate(request));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid search books by isbn request", violations);
    }

    return bookRepository.findBooksByIsbn(
        request.getTerm(),
        request.getBeforeYear(),
        request.getAfterYear(),
        PageRequest.of(request.getPageNumber(), request.getPageSize())
    );
  }

  @Override
  public Page<Book> findBooksByInventoryNumber(SearchBooksDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertConstrainViolationSetToMap(validator.validate(request));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid search books by inventory number request", violations);
    }

    return bookRepository.findBooksByInventoryNumber(
        request.getTerm(),
        request.getBeforeYear(),
        request.getAfterYear(),
        PageRequest.of(request.getPageNumber(), request.getPageSize())
    );
  }

  @Override
  public CoverImageDto getCoverImage(Long id) {
    Optional<Book> b = bookRepository.getBookById(id);
    String coverImageString = getCoverImageString(id, b);

    Path coverImagePath = Paths.get(coverImageString);

    byte[] coverImageBytes;
    try {
      coverImageBytes = Files.readAllBytes(coverImagePath);
    } catch (IOException e) {
      Map<String, String> violations = new HashMap<>();
      violations.put("book", "Unable to read cover image file for book with id " + id);
      throw new BadRequestDetailsException("Unable to get book cover", violations);
    }

    MediaType mediaType = MediaTypeFactory.getMediaType(new ClassPathResource(coverImageString))
        .orElse(MediaType.APPLICATION_OCTET_STREAM);

    return CoverImageDto.builder()
        .imageName(coverImagePath.getFileName().toString())
        .imageData(coverImageBytes)
        .imageContentType(mediaType.getType())
        .build();
  }

  @NonNull
  private static String getCoverImageString(Long id, Optional<Book> b) {
    if (b.isEmpty()) {
      Map<String, String> violations = new HashMap<>();
      violations.put("book", "Book with id " + id + " is not found");
      throw new BadRequestDetailsException("Unable to get book cover", violations);
    }

    Book book = b.get();
    String coverImageString = book.getCoverImage();
    if (coverImageString == null) {
      Map<String, String> violations = new HashMap<>();
      violations.put("book", "Book with id " + id + " has no cover");
      throw new BadRequestDetailsException("Unable to get book cover", violations);
    }
    return coverImageString;
  }

  @Override
  @Transactional
  public void createBook(CreateBookDto request, MultipartFile coverImage) throws BadRequestDetailsException {
    Long nextBookId = bookRepository.getCurrentBookIdSequenceValue() + 1;

    Map<String, String> violations = convertConstrainViolationSetToMap(validator.validate(request));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid create book request", violations);
    }

    BookType bookType = bookTypeService.getBookTypeByString(request.getType().trim(), true);
    BookField bookField = bookFieldService.getBookFieldByString(request.getField().trim(), true);
    Optional<Book> bookWithSameTitle = bookRepository.getBookByTitle(request.getTitle().trim());
    if (bookWithSameTitle.isPresent()) {
      violations.put("title", "There is another book existed with the same title");
      throw new BadRequestDetailsException("Invalid create book request", violations);
    }

    Book book = Book.builder()
        .title(request.getTitle().trim())
        .authors(request.getAuthors().trim())
        .publisher(request.getPublisher().trim())
        .type(bookType)
        .field(bookField)
        .year(request.getYear())
        .edition(request.getEdition())
        .isbn(request.getIsbn())
        .inventoryNumber(generateInventoryNumber(nextBookId, request.getType(), request.getYear()))
        .coverImage(getCoverImagePath(request.getCoverUrl(), coverImage, request.getIsbn().trim()))
        .quantity(request.getQuantity())
        .build();

    bookRepository.save(book);
  }

  @Override
  @Transactional
  public void updateBook(Long id, UpdateBookDto request, MultipartFile coverImage) throws BadRequestDetailsException {
    Map<String, String> violations = convertConstrainViolationSetToMap(validator.validate(request));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid create book request", violations);
    }

    if (request.getTitle() != null) {
      Optional<Book> bookWithSameTitle = bookRepository.getBookByTitle(request.getTitle().trim());
      if (bookWithSameTitle.isPresent() && id != bookWithSameTitle.get().getId()) {
        violations.put("title", "A book with the same title is already stored in the library");
        throw new BadRequestDetailsException("Invalid create book request", violations);
      }
    }

    Optional<Book> b = bookRepository.getBookById(id);
    if (b.isEmpty()) {
      violations.put("id", "There is no book with id " + id);
      throw new BadRequestDetailsException("Invalid create book request", violations);
    }

    Book book = b.get();
    updateIfNotNull(request.getTitle(), book::setTitle);
    updateIfNotNull(request.getAuthors(), book::setAuthors);
    updateIfNotNull(request.getPublisher(), book::setPublisher);
    updateIfNotNull(bookTypeService.getBookTypeByString(request.getType(), true),
        book::setType);
    updateIfNotNull(bookFieldService.getBookFieldByString(request.getField(), true),
        book::setField);
    updateIfNotNull(request.getYear(), book::setYear);
    updateIfNotNull(request.getEdition(), book::setEdition);
    updateIfNotNull(request.getIsbn(), book::setIsbn);
    book.setInventoryNumber(generateInventoryNumber(id, book.getType().getName(), book.getYear()));
    updateIfNotNull(getCoverImagePath(request.getCoverUrl(), coverImage, request.getIsbn()),
        book::setCoverImage);

    if (request.getQuantity() != null) {
      Integer countOpenedBorrowEntries = borrowEntryRepository.countOpenedBorrowEntriesByBook(book);
      if (request.getQuantity() < countOpenedBorrowEntries) {
        violations.put("quantity", "New quantity of a book can't be smaller than number of issued books");
        throw new BadRequestDetailsException("Invalid create book request", violations);
      }
      book.setQuantity(request.getQuantity());
    }

    bookRepository.save(book);
  }

  private String generateInventoryNumber(Long id, String type, int year) {
    return id + " CSST " + type.toUpperCase().substring(0, 2) + " " + year;
  }

  private String getCoverImagePath(String coverImageUrl, MultipartFile coverImage, String isbn) {
    if (coverImage != null) {
      return imageFileService.uploadImage(coverImage, isbn);
    }

    if (coverImageUrl != null) {
      return imageFileService.downloadImage(coverImageUrl, isbn);
    }

    return null;
  }

  private <T> void updateIfNotNull(T value, Consumer<T> updater) {
    if (value != null) {
      updater.accept(value);
    }
  }

  @Override
  @Transactional
  public void deleteBookById(Long id) {
    Book book = findBookById(id);

    int countBorrowEntries = borrowEntryRepository.countClosedBorrowEntries(book)
        + borrowEntryRepository.countOpenedBorrowEntriesByBook(book);
    if (countBorrowEntries > 0) {
      Map<String, String> violations = new HashMap<>();
      violations.put("book", "The book with id " + id + " has one or more borrow entries connected to it");
      throw new BadRequestDetailsException("Unable to delete book", violations);
    }

    if (book.getCoverImage() != null) {
      File coverImage = new File(book.getCoverImage());
      if (coverImage.exists()) {
        coverImage.delete();
      }
    }

    bookRepository.deleteBookById(id);
  }
}
