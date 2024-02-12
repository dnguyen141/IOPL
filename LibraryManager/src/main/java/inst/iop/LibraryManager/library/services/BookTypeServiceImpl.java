package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.library.entities.BookType;
import inst.iop.LibraryManager.library.entities.constrains.TypeConstrain;
import inst.iop.LibraryManager.library.repositories.BookTypeRepository;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static inst.iop.LibraryManager.utilities.BindingResultHandler.*;

@Data
@RequiredArgsConstructor
@Service
public class BookTypeServiceImpl implements BookTypeService {
  private final BookTypeRepository bookTypeRepository;

  @Override
  public List<BookType> getAllBookTypes() {
    return bookTypeRepository.getAllBookType();
  }

  @Override
  public BookType getBookTypeById(Long id) {
    Optional<BookType> bt = bookTypeRepository.getBookTypeById(id);
    if (bt.isEmpty()) {
      Map<String, Object> violations = new HashMap<>();
      violations.put("id", "There is no book type with id " + id);
      throw new BadRequestDetailsException(violations);
    }
    return bt.get();
  }

  @Override
  public BookType getBookTypeFromString(@TypeConstrain String type, BindingResult bindingResult) {
    return getBookTypeFromString(type, bindingResult, false);
  }

  @Override
  public BookType getBookTypeFromString(@TypeConstrain String type, BindingResult bindingResult,
                                        boolean isCreateRequest) {
    Map<String, Object> violations = handleBindingResult(bindingResult);
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException(violations);
    }

    Optional<BookType> bookType = bookTypeRepository.getBookTypeByString(type);

    if (bookType.isEmpty() && isCreateRequest) {
      BookType bt = BookType.builder()
          .name(type)
          .build();
      bookTypeRepository.save(bt);
      return bt;
    }

    Map<String, Object> violations = new HashMap<>();
    violations.put("name", "There is no book type with name " + type);
    throw new BadRequestDetailsException(violations);
  }

  @Override
  @Transactional
  public void createType(@TypeConstrain String type, BindingResult bindingResult) {
    Map<String, Object> violations = handleBindingResult(bindingResult);
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException(violations);
    }

    Optional<BookType> bt = bookTypeRepository.getBookTypeByString(type);
    if (bt.isEmpty()) {
      BookType bookType = BookType.builder()
          .name(type)
          .build();
      bookTypeRepository.save(bookType);
    }
  }

  @Override
  @Transactional
  public void updateBookTypeById(Long id, @TypeConstrain String name, BindingResult bindingResult) {
    Map<String, Object> violations = handleBindingResult(bindingResult);
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException(violations);
    }

    bookTypeRepository.updateBookTypeById(id, name);
  }

  @Override
  @Transactional
  public void deleteTypeById(Long id) {
    Optional<BookType> bt = bookTypeRepository.getBookTypeById(id);
    if (bt.isPresent()) {
      bookTypeRepository.deleteBookTypeById(id);
    }
  }
}
