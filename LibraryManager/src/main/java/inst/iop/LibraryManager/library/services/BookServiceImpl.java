package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.library.dtos.CreateBookDto;
import inst.iop.LibraryManager.library.dtos.UpdateBookDto;
import inst.iop.LibraryManager.library.entities.Book;
import inst.iop.LibraryManager.library.entities.BookField;
import inst.iop.LibraryManager.library.entities.BookType;
import inst.iop.LibraryManager.library.entities.constrains.PageNumberConstrain;
import inst.iop.LibraryManager.library.entities.constrains.PageSizeConstrain;
import inst.iop.LibraryManager.library.entities.constrains.YearConstrain;
import inst.iop.LibraryManager.library.repositories.BookRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Validated
public class BookServiceImpl implements BookService {
  private final BookRepository bookRepository;
  private final BookTypeService bookTypeService;
  private final BookFieldService bookFieldService;
  private final ImageFileService imageFileService;

  @Override
  public Page<Book> getAllBooks(@PageNumberConstrain Integer pageNumber, @PageSizeConstrain Integer pageSize) {
    return bookRepository.findAllBooks(PageRequest.of(pageNumber, pageSize));
  }

  @Override
  public Optional<Book> getBookFromId(Long id) {
    return bookRepository.findBookById(id);
  }

  @Override
  public Optional<Book> getBookFromTitle(String title) {
    return bookRepository.findBookByTitle(title);
  }

  @Override
  public Page<Book> findBooks(String title, String authors, String publisher, String type, String field,
                              @YearConstrain Integer beforeYear, @YearConstrain Integer afterYear, String isbn,
                              String inventoryNumber, @PageNumberConstrain Integer pageNumber,
                              @PageSizeConstrain Integer pageSize) {
    return bookRepository.findBooks(
        title,
        authors,
        publisher,
        type,
        field,
        beforeYear,
        afterYear,
        isbn,
        inventoryNumber,
        PageRequest.of(pageNumber, pageSize)
    );
  }

  @Override
  public Page<Book> findBooksByTitle(String title, @YearConstrain Integer beforeYear, @YearConstrain Integer afterYear,
                                     @PageNumberConstrain Integer pageNumber, @PageSizeConstrain Integer pageSize) {
    return bookRepository.findBooksByTitle(
        title,
        beforeYear,
        afterYear,
        PageRequest.of(pageNumber, pageSize)
    );
  }

  @Override
  public Page<Book> findBooksByAuthors(String authors, @YearConstrain Integer beforeYear,
                                       @YearConstrain Integer afterYear, @PageNumberConstrain Integer pageNumber,
                                       @PageSizeConstrain Integer pageSize) {
    return bookRepository.findBooksByAuthors(
        authors,
        beforeYear,
        afterYear,
        PageRequest.of(pageNumber, pageSize)
    );
  }

  @Override
  public Page<Book> findBooksByPublisher(String publisher, @YearConstrain Integer beforeYear,
                                         @YearConstrain Integer afterYear, @PageNumberConstrain Integer pageNumber,
                                         @PageSizeConstrain Integer pageSize) {
    return bookRepository.findBooksByPublisher(
        publisher,
        beforeYear,
        afterYear,
        PageRequest.of(pageNumber, pageSize)
    );
  }

  @Override
  public Page<Book> findBooksByType(String type, @YearConstrain Integer beforeYear, @YearConstrain Integer afterYear,
                                    @PageNumberConstrain Integer pageNumber, @PageSizeConstrain Integer pageSize) {
    return bookRepository.findBooksByType(
        type,
        beforeYear,
        afterYear,
        PageRequest.of(pageNumber, pageSize)
    );
  }

  @Override
  public Page<Book> findBooksByField(String field, @YearConstrain Integer beforeYear, @YearConstrain Integer afterYear,
                                     @PageNumberConstrain Integer pageNumber, @PageSizeConstrain Integer pageSize)
      throws IllegalArgumentException {
    return bookRepository.findBooksByField(
        field,
        beforeYear,
        afterYear,
        PageRequest.of(pageNumber, pageSize)
    );
  }

  @Override
  public Page<Book> findBooksByIsbn(String isbn, @YearConstrain Integer beforeYear, @YearConstrain Integer afterYear,
                                    @PageNumberConstrain Integer pageNumber, @PageSizeConstrain Integer pageSize) {
    return bookRepository.findBooksByIsbn(isbn, beforeYear, afterYear, PageRequest.of(pageNumber, pageSize));
  }

  @Override
  public Page<Book> findBooksByInventoryNumber(String inventoryNumber, @YearConstrain Integer beforeYear,
                                               @YearConstrain Integer afterYear,
                                               @PageNumberConstrain Integer pageNumber,
                                               @PageSizeConstrain Integer pageSize) {
    return bookRepository.findBooksByInventoryNumber(inventoryNumber, beforeYear, afterYear,
        PageRequest.of(pageNumber, pageSize));
  }

  @Override
  @Transactional
  public void createBook(@Valid CreateBookDto bookDto) throws IllegalArgumentException {
    BookType bookType = bookTypeService.getBookTypeFromString(bookDto.getType().trim(), true);
    Optional<BookField> field = bookFieldService.getBookFieldFromString(bookDto.getField().trim(), true);

    if (bookType == null) {
      throw new IllegalArgumentException("Unable to get book type with given input");
    }

    if (field.isEmpty()) {
      throw new IllegalArgumentException("Unable to get book field with given input");
    }

    BookField bookField = field.get();

    Book book = Book.builder()
        .title(bookDto.getTitle().trim())
        .authors(bookDto.getAuthors().trim())
        .publisher(bookDto.getPublisher().trim())
        .type(bookType)
        .field(bookField)
        .year(bookDto.getYear())
        .edition(bookDto.getEdition())
        .isbn(bookDto.getIsbn().trim())
        .inventoryNumber(generateInventoryNumber(bookDto.getType().trim(), bookDto.getYear()))
        .coverImage(bookDto.getCoverImage() != null ?
            imageFileService.uploadImage(bookDto.getCoverImage(), bookDto.getIsbn().trim()) : null)
        .quantity(bookDto.getQuantity())
        .available(bookDto.getQuantity())
        .build();

    bookRepository.save(book);
  }

  @Override
  @Transactional
  public void updateBook(Long id, @Valid UpdateBookDto bookDto) throws IllegalArgumentException {
    Book book = getBookFromId(id).orElseThrow(() -> new IllegalArgumentException("Unable to get book with given input"));

    updateIfNotNull(bookDto.getTitle(), book::setTitle);
    updateIfNotNull(bookDto.getAuthors(), book::setAuthors);
    updateIfNotNull(bookDto.getPublisher(), book::setPublisher);
    book.setType(bookTypeService.getBookTypeFromString(bookDto.getType().trim()));
    bookFieldService.getBookFieldFromString(bookDto.getField().trim()).ifPresent(book::setField);
    updateIfNotNull(bookDto.getYear(), book::setYear);
    updateIfNotNull(bookDto.getEdition(), book::setEdition);
    updateIfNotNull(bookDto.getIsbn(), book::setIsbn);
    updateIfNotNull(bookDto.getInventoryNumber(), book::setInventoryNumber);

    if (bookDto.getQuantity() != null) {
      updateBookQuantity(book, bookDto.getQuantity());
    }

    updateIfNotNull(imageFileService.uploadImage(bookDto.getCoverImage(), book.getIsbn()), book::setCoverImage);
    bookRepository.save(book);
  }

  private <T> void updateIfNotNull(T value, Consumer<T> updater) {
    if (value != null) {
      updater.accept(value);
    }
  }

  private void updateBookQuantity(Book book, Integer newQuantity) throws IllegalArgumentException {
    int oldQuantity = book.getQuantity();
    int oldAvailable = book.getAvailable();
    int newAvailable = oldAvailable + newQuantity - oldQuantity;

    if (newAvailable > 0) {
      book.setQuantity(newQuantity);
      book.setAvailable(newAvailable);
      return;
    }

    throw new IllegalArgumentException("New amount of book must be consistent with number of borrow entries");
  }

  @Override
  @Transactional
  public void deleteBookById(Long id) {
    bookRepository.deleteBookById(id);
  }

  private String generateInventoryNumber(String type, int year) {
    int bookId = bookRepository.getNumberOfBooks() == 0 ? 100 : bookRepository.getHighestBookId() + 1;
    String bookType = type.toUpperCase().substring(0, 2);
    return bookId + " CSST " + bookType + " " + year;
  }
}
