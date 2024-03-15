package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.library.dtos.CreateUpdateFieldDto;
import inst.iop.LibraryManager.library.entities.BookField;
import inst.iop.LibraryManager.library.entities.constraints.FieldConstraint;
import inst.iop.LibraryManager.library.repositories.BookFieldRepository;
import inst.iop.LibraryManager.library.repositories.BookRepository;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static inst.iop.LibraryManager.utilities.ConstraintViolationSetHandler.convertSetToMap;

@RequiredArgsConstructor
@Service
public class BookFieldServiceImpl implements BookFieldService {
  private final BookFieldRepository bookFieldRepository;
  private final BookRepository bookRepository;
  private final Validator validator;

  @Override
  public List<BookField> listAllBookFields() {
    return bookFieldRepository.getAllBookFields();
  }

  @Override
  public BookField getBookFieldById(Long id) throws BadRequestDetailsException {
    Optional<BookField> bf = bookFieldRepository.getBookFieldById(id);
    if (bf.isEmpty()) {
      Map<String, String> violations = new HashMap<>();
      violations.put("id", "There is no book field with id " + id);
      throw new BadRequestDetailsException("Unable to get book field by string", violations);
    }
    return bf.get();
  }

  @Override
  public BookField getBookFieldByString(@Valid @FieldConstraint String name, boolean isCreateBookRequest)
      throws BadRequestDetailsException {
    Map<String, String> violations = convertSetToMap(validator.validate(name));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Unable to get book field by string", violations);
    }

    Optional<BookField> bf = bookFieldRepository.getBookFieldByString(name);

    if (bf.isEmpty() && isCreateBookRequest) {
      BookField bookField = BookField.builder()
          .name(name)
          .build();
      bookFieldRepository.save(bookField);
      return bookField;
    }

    return bf.orElse(null);
  }

  @Override
  @Transactional
  public void createField(CreateUpdateFieldDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertSetToMap(validator.validate(request));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid create book field request", violations);
    }

    Optional<BookField> bf = bookFieldRepository.getBookFieldByString(request.getName().trim());
    if (bf.isEmpty()) {
      BookField bookField = BookField.builder()
          .name(request.getName())
          .build();
      bookFieldRepository.save(bookField);
      return;
    }

    violations.put("name", "Book field with same name is already existed");
    throw new BadRequestDetailsException("Invalid create book field request", violations);
  }

  @Override
  @Transactional
  public void updateBookFieldById(Long id, CreateUpdateFieldDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertSetToMap(validator.validate(request));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid update book field by id request", violations);
    }

    Optional<BookField> bookField = bookFieldRepository.getBookFieldByString(request.getName().trim());
    if (bookField.isPresent() && bookField.get().getId() != id) {
      violations.put("name", "There is another book field with the same name");
      throw new BadRequestDetailsException("Invalid update book field request", violations);
    }

    bookFieldRepository.updateBookFieldById(id, request.getName());
  }

  @Override
  @Transactional
  public void deleteField(Long id) throws BadRequestDetailsException {
    Optional<BookField> bf = bookFieldRepository.getBookFieldById(id);
    if (bf.isEmpty()) {
      return;
    }

    Integer booksWithField = bookRepository.countBooksByField(bf.get());
    if (booksWithField == 0) {
      bookFieldRepository.deleteBookFieldById(id);
      return;
    }

    Map<String, String> details = new HashMap<>();
    details.put("field", "There are still books that have this field");
    throw new BadRequestDetailsException("Unable to delete book field", details);
  }
}
