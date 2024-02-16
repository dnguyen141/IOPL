package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.library.dtos.CreateBorrowEntryDto;
import inst.iop.LibraryManager.library.dtos.UpdateBorrowEntryDto;
import inst.iop.LibraryManager.library.entities.BorrowEntry;
import inst.iop.LibraryManager.library.entities.constraints.BorrowStatusConstraint;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import org.springframework.data.domain.Page;

public interface BorrowEntryService {

  BorrowEntry getBorrowEntryById(Long id) throws BadRequestDetailsException;

  Page<BorrowEntry> listBorrowEntriesByStatus(String status, Integer pageNumber, Integer pageSize)
      throws BadRequestDetailsException;

  Page<BorrowEntry> listBorrowEntriesByUsernameAndStatus(String username, @BorrowStatusConstraint String status,
                                                         Integer pageNumber, Integer pageSize)
      throws BadRequestDetailsException;

  Page<BorrowEntry> listBorrowEntriesByBookIdAndStatus(Long bookId, String status, Integer pageNumber, Integer pageSize)
      throws BadRequestDetailsException;

  Integer getBookAvailable(Long bookID);

  void createBorrowEntry(CreateBorrowEntryDto request) throws BadRequestDetailsException;

  void updateBorrowEntryById(Long id, UpdateBorrowEntryDto request) throws BadRequestDetailsException;

  void deleteBorrowEntryById(Long id);
}
