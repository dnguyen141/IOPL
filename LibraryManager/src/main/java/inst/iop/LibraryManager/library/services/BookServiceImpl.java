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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static inst.iop.LibraryManager.utilities.ConstraintViolationSetHandler.convertSetToMap;

@RequiredArgsConstructor
@Service
@Slf4j
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepository;
  private final BorrowEntryRepository borrowEntryRepository;
  private final BookTypeService bookTypeService;
  private final BookFieldService bookFieldService;
  private final ImageFileService imageFileService;
  private final Validator validator;

  public Page<Book> listAllBooks(ListAllBooksDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertSetToMap(validator.validate(request));
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
    Map<String, String> violations = convertSetToMap(validator.validate(request));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid search books by term request", violations);
    }

    String term = request.getTerm();
    return bookRepository.findBooks(term, term, term, term, term, request.getBeforeYear(), request.getAfterYear(), term,
        term, PageRequest.of(request.getPageNumber(), request.getPageSize()));
  }

  @Override
  public Page<Book> findBooksByTitle(SearchBooksDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertSetToMap(validator.validate(request));
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
    Map<String, String> violations = convertSetToMap(validator.validate(request));
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
    Map<String, String> violations = convertSetToMap(validator.validate(request));
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
    Map<String, String> violations = convertSetToMap(validator.validate(request));
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
    Map<String, String> violations = convertSetToMap(validator.validate(request));
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
    Map<String, String> violations = convertSetToMap(validator.validate(request));
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
    Map<String, String> violations = convertSetToMap(validator.validate(request));
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
  public String getCoverImagePath(Long id) {
    Optional<Book> b = bookRepository.getBookById(id);
    if (b.isEmpty()) {
      Map<String, String> violations = new HashMap<>();
      violations.put("book", "Book with id " + id + " is not found");
      throw new BadRequestDetailsException("Unable to get book cover", violations);
    }

    Book book = b.get();
    String coverImageString = book.getCoverImage();
    if (coverImageString == null) {
      return null;
    }

    Path coverImagePath = Paths.get(coverImageString);
    return removeParentFolders(coverImagePath, 3).toString();
  }

  public Path removeParentFolders(Path path, int count) {
    int elementsToRemove = Math.min(count, path.getNameCount());
    return path.subpath(elementsToRemove, path.getNameCount());
  }

  @Override
  @Transactional
  public Book createBook(CreateBookDto request, MultipartFile coverImage) throws BadRequestDetailsException {
    long nextBookId = 100;
    Optional<Book> firstBook = bookRepository.findFirstByOrderById();
    if (firstBook.isPresent()) {
      nextBookId = bookRepository.getCurrentBookIdSequenceValue() + 1;
    }

    Map<String, String> violations = convertSetToMap(validator.validate(request));
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
        .coverImage(getCoverImagePath(request.getCoverUrl(), coverImage, nextBookId))
        .quantity(request.getQuantity())
        .build();

    return bookRepository.save(book);
  }

  @Override
  @Transactional
  public void updateBook(Long id, UpdateBookDto request, MultipartFile coverImage) throws BadRequestDetailsException {
    Map<String, String> violations = convertSetToMap(validator.validate(request));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid create book request", violations);
    }

    Optional<Book> bookWithSameTitle = bookRepository.getBookByTitle(request.getTitle().trim());
    if (bookWithSameTitle.isPresent() && id != bookWithSameTitle.get().getId()) {
      violations.put("title", "A book with the same title is already stored in the library");
      throw new BadRequestDetailsException("Invalid create book request", violations);
    }

    Book book = bookRepository.getBookById(id).orElseThrow(() -> {
      violations.put("id", "There is no book with id " + id);
      return new BadRequestDetailsException("Invalid create book request", violations);
    });

    Integer countOpenedBorrowEntries = borrowEntryRepository.countOpenedBorrowEntriesByBook(book);
    if (request.getQuantity() < countOpenedBorrowEntries) {
      violations.put("quantity", "New quantity of a book can't be smaller than number of issued books");
      throw new BadRequestDetailsException("Invalid create book request", violations);
    }
    book.setQuantity(request.getQuantity());

    bookRepository.updateBookById(
        book.getId(),
        request.getTitle().trim(),
        request.getAuthors().trim(),
        request.getPublisher().trim(),
        bookTypeService.getBookTypeByString(request.getType().trim(), false),
        bookFieldService.getBookFieldByString(request.getField().trim(), false),
        request.getYear(),
        request.getEdition(),
        request.getIsbn().trim(),
        generateInventoryNumber(id, book.getType().getName(), book.getYear()),
        request.getQuantity(),
        getCoverImagePath(request.getCoverUrl(), coverImage, book.getId())
    );
  }

  public String generateInventoryNumber(Long id, String type, int year) {
    return id + " CSST " + type.toUpperCase().substring(0, 2) + " " + year;
  }

  public String getCoverImagePath(String coverImageUrl, MultipartFile coverImage, Long bookId) {
    if (coverImage != null) {
      return imageFileService.uploadImage(coverImage, bookId);
    }

    if (coverImageUrl != null) {
      return imageFileService.downloadImage(coverImageUrl, bookId);
    }

    return null;
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

  @Override
  @Transactional
  public void importBooksFromExcelFile(MultipartFile excelFile) {
    try {
      File uploadedExcelFile = convertToFile(excelFile);
      FileInputStream fis = new FileInputStream(uploadedExcelFile);
      var workbook = new XSSFWorkbook(fis);
      var sheet = workbook.getSheetAt(0);

      int i = 0;
      var properties = new ArrayList<String>();
      for (Iterator<Row> rowIterator = sheet.iterator(); rowIterator.hasNext(); rowIterator.next(), i++) {
        Row currentRow = rowIterator.next();
        Map<String, String> data = new HashMap<>();
        int j = 0;
        for (Cell cell : currentRow) {
          if (i == 0) {
            properties.add(cell.getStringCellValue().toLowerCase());
          } else {
            data.put(properties.get(j), cell.getStringCellValue());
          }
          j++;
        }

        if (i == 0) {
          log.info("Properties: " + properties);
          continue;
        }

        Book book = Book.builder()
            .id(Long.parseLong(data.get("id")))
            .title(data.get("title"))
            .authors(data.get("authors"))
            .publisher(data.get("publisher"))
            .type(bookTypeService.getBookTypeByString(data.get("type"), true))
            .field(bookFieldService.getBookFieldByString(data.get("field"), true))
            .year(Integer.parseInt(data.get("year")))
            .edition(Integer.parseInt(data.get("edition")))
            .isbn(data.get("isbn"))
            .inventoryNumber(data.get("inventorynumber"))
            .coverImage(null)
            .quantity(Integer.parseInt(data.get("quantity")))
            .build();
        bookRepository.save(book);
      }
    } catch (IOException e) {
      Map<String, String> violations = new HashMap<>();
      violations.put("file", e.getMessage());
      violations.put("stackTrace", Arrays.toString(e.getStackTrace()));
      throw new BadRequestDetailsException("Unable to import books from excel file", violations);
    }
  }

  private File convertToFile(MultipartFile file) throws IOException {
    String fileName = file.getOriginalFilename();
    if (fileName == null || fileName.isEmpty()) {
      throw new IOException("File name is empty");
    }
    File convertedFile = new File("src/main/resources/" + file.getOriginalFilename());
    boolean isCreated = convertedFile.createNewFile();
    if (!isCreated) {
      throw new IOException("Can't save uploaded file: " + file.getOriginalFilename());
    }
    FileOutputStream fos = new FileOutputStream(convertedFile);
    fos.write(file.getBytes());
    fos.close();
    return convertedFile;
  }
}
