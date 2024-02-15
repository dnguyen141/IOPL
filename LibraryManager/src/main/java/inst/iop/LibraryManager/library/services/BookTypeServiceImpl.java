package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.library.dtos.CreateUpdateTypeDto;
import inst.iop.LibraryManager.library.entities.BookType;
import inst.iop.LibraryManager.library.entities.constraints.TypeConstraint;
import inst.iop.LibraryManager.library.repositories.BookRepository;
import inst.iop.LibraryManager.library.repositories.BookTypeRepository;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import jakarta.transaction.Transactional;

import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static inst.iop.LibraryManager.utilities.ConstraintViolationSetHandler.convertConstrainViolationSetToMap;

@Data
@RequiredArgsConstructor
@Service
public class BookTypeServiceImpl implements BookTypeService {
  private final BookTypeRepository bookTypeRepository;
  private final BookRepository bookRepository;
  private final Validator validator;

  @Override
  public List<BookType> listAllBookTypes() {
    return bookTypeRepository.getAllBookType();
  }

  @Override
  public BookType getBookTypeById(Long id) throws BadRequestDetailsException {
    Optional<BookType> bt = bookTypeRepository.getBookTypeById(id);
    if (bt.isEmpty()) {
      Map<String, String> violations = new HashMap<>();
      violations.put("id", "There is no book type with id " + id);
      throw new BadRequestDetailsException("Invalid get book field by id request", violations);
    }
    return bt.get();
  }

  @Override
  public BookType getBookTypeByString(@Valid @TypeConstraint String name, boolean isCreateBookRequest)
      throws BadRequestDetailsException {
    Map<String, String> violations = convertConstrainViolationSetToMap(validator.validate(name));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid create type request", violations);
    }

    Optional<BookType> bt = bookTypeRepository.getBookTypeByString(name);

    if (bt.isEmpty() && isCreateBookRequest) {
      BookType bookType = BookType.builder()
          .name(name)
          .build();
      bookTypeRepository.save(bookType);
      return bookType;
    }

    return bt.orElse(null);
  }

  @Override
  @Transactional
  public void createType(CreateUpdateTypeDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertConstrainViolationSetToMap(validator.validate(request));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid create type request", violations);
    }

    Optional<BookType> bt = bookTypeRepository.getBookTypeByString(request.getName().trim());
    if (bt.isEmpty()) {
      BookType bookType = BookType.builder()
          .name(request.getName())
          .build();
      bookTypeRepository.save(bookType);
      return;
    }

    violations.put("name", "Book type with same name is already existed");
    throw new BadRequestDetailsException("Invalid create book type request", violations);
  }

  @Override
  @Transactional
  public void updateBookTypeById(Long id, CreateUpdateTypeDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertConstrainViolationSetToMap(validator.validate(request));
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid update type request", violations);
    }

    Optional<BookType> bookType = bookTypeRepository.getBookTypeByString(request.getName().trim());
    if (bookType.isPresent() && bookType.get().getId() != id) {
      violations.put("name", "There is another book type with the same name");
      throw new BadRequestDetailsException("Invalid update book type request", violations);
    }

    bookTypeRepository.updateBookTypeById(id, request.getName());
  }

  @Override
  @Transactional
  public void deleteTypeById(Long id) throws BadRequestDetailsException {
    Optional<BookType> bt = bookTypeRepository.getBookTypeById(id);
    if (bt.isEmpty()) {
      return;
    }

    Integer booksWithType = bookRepository.countBooksByType(bt.get());
    if (booksWithType == 0) {
      bookTypeRepository.deleteBookTypeById(id);
      return;
    }

    Map<String, String> details = new HashMap<>();
    details.put("type", "There are still books that have this type");
    throw new BadRequestDetailsException("Unable to delete book type", details);
  }
}
