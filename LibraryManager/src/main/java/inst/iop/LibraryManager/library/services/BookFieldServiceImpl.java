package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.library.entities.BookField;
import inst.iop.LibraryManager.library.entities.constrains.FieldConstrain;
import inst.iop.LibraryManager.library.entities.constrains.TypeConstrain;
import inst.iop.LibraryManager.library.repositories.BookFieldRepository;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static inst.iop.LibraryManager.utilities.BindingResultHandler.handleBindingResult;

@Data
@RequiredArgsConstructor
@Service
public class BookFieldServiceImpl implements BookFieldService {
  private final BookFieldRepository bookFieldRepository;

  @Override
  public List<BookField> getAllBookFields() {
    return bookFieldRepository.getAllBookFields();
  }

  @Override
  public Optional<BookField> getBookFieldById(Long id) {
    return bookFieldRepository.getBookFieldById(id);
  }

  @Override
  public Optional<BookField> getBookFieldFromString(@TypeConstrain String field) {
    return getBookFieldFromString(field, false);
  }

  @Override
  public Optional<BookField> getBookFieldFromString(@FieldConstrain String field, boolean isCreateRequest) {
    Optional<BookField> bookField = bookFieldRepository.getBookFieldByString(field);

    if (bookField.isEmpty() && isCreateRequest) {
      BookField bf = BookField.builder()
          .name(field)
          .build();
      bookFieldRepository.save(bf);
      return Optional.of(bf);
    }

    return bookField;
  }

  @Override
  @Transactional
  public void createField(@FieldConstrain String field, BindingResult bindingResult) {
    Map<String, Object> violations = handleBindingResult(bindingResult);
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException(violations);
    }

    Optional<BookField> bf = bookFieldRepository.getBookFieldByString(field);
    if (bf.isEmpty()) {
      BookField bookField = BookField.builder()
          .name(field)
          .build();
      bookFieldRepository.save(bookField);
    }
  }

  @Override
  @Transactional
  public void updateBookFieldById(Long id, @FieldConstrain String name, BindingResult bindingResult) {
    Map<String, Object> violations = handleBindingResult(bindingResult);
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException(violations);
    }

    bookFieldRepository.updateBookFieldById(id, name);
  }

  @Override
  @Transactional
  public void deleteField(Long id) {
    Optional<BookField> bf = bookFieldRepository.getBookFieldById(id);
    if (bf.isPresent()) {
      bookFieldRepository.deleteBookFieldById(id);
    }
  }
}
