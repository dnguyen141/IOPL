package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.library.dtos.CreateUpdateFieldDto;
import inst.iop.LibraryManager.library.entities.BookField;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import org.springframework.stereotype.Service;

import java.util.List;

public interface BookFieldService {

  List<BookField> listAllBookFields();

  BookField getBookFieldById(Long id) throws BadRequestDetailsException;

  BookField getBookFieldByString(String name, boolean isCreateBookRequest) throws BadRequestDetailsException;

  void createField(CreateUpdateFieldDto request) throws BadRequestDetailsException;

  void updateBookFieldById(Long id, CreateUpdateFieldDto request) throws BadRequestDetailsException;

  void deleteField(Long id) throws BadRequestDetailsException;
}
