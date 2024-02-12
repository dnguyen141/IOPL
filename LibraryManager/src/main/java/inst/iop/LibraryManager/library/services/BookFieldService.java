package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.library.entities.BookField;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

public interface BookFieldService {

  List<BookField> getAllBookFields();

  Optional<BookField> getBookFieldById(Long id);

  Optional<BookField> getBookFieldFromString(String field);

  Optional<BookField> getBookFieldFromString(String field, boolean isCreateRequest);

  void createField(String field, BindingResult bindingResult);

  void updateBookFieldById(Long id, String name, BindingResult bindingResult);

  void deleteField(Long id);
}
