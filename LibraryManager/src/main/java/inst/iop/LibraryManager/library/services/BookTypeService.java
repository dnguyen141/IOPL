package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.library.entities.BookType;
import inst.iop.LibraryManager.library.entities.constrains.TypeConstrain;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

public interface BookTypeService {

  List<BookType> getAllBookTypes();

  BookType getBookTypeById(Long id);

  BookType getBookTypeFromString(String type, BindingResult bindingResult);

  BookType getBookTypeFromString(String type, boolean isCreateRequest);

  BookType getBookTypeFromString(String type, BindingResult bindingResult, boolean isCreateRequest);

  void updateBookTypeById(Long id, String name, BindingResult bindingResult);

  void deleteTypeById(Long id);
}
