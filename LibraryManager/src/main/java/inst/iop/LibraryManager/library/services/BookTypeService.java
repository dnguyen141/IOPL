package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.library.dtos.CreateUpdateTypeDto;
import inst.iop.LibraryManager.library.entities.BookType;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;

import java.util.List;

public interface BookTypeService {

  List<BookType> listAllBookTypes();

  BookType getBookTypeById(Long id) throws BadRequestDetailsException;

  BookType getBookTypeByString(String name, boolean isCreateBookRequest) throws BadRequestDetailsException;

  void createType(CreateUpdateTypeDto request) throws BadRequestDetailsException;

  void updateBookTypeById(Long id, CreateUpdateTypeDto name) throws BadRequestDetailsException;

  void deleteTypeById(Long id) throws BadRequestDetailsException;
}
