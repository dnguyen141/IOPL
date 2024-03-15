package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.library.dtos.CreateBorrowEntryDto;
import inst.iop.LibraryManager.library.dtos.ListBorrowEntriesByBookIdAndStatusDto;
import inst.iop.LibraryManager.library.dtos.ListBorrowEntriesByStatusDto;
import inst.iop.LibraryManager.library.dtos.UpdateBorrowEntryDto;
import inst.iop.LibraryManager.library.entities.BorrowEntry;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import org.springframework.data.domain.Page;

public interface BorrowEntryService {

  BorrowEntry getBorrowEntryById(Long id) throws BadRequestDetailsException;

  Page<BorrowEntry> listBorrowEntriesByStatus(ListBorrowEntriesByStatusDto request) throws BadRequestDetailsException;

  Page<BorrowEntry> listBorrowEntriesByUsernameAndStatus(String username, ListBorrowEntriesByStatusDto request)
      throws BadRequestDetailsException;

  Page<BorrowEntry> listBorrowEntriesByBookIdAndStatus(ListBorrowEntriesByBookIdAndStatusDto request)
      throws BadRequestDetailsException;

  Integer getBookAvailability(Long bookID);

  BorrowEntry createBorrowEntry(CreateBorrowEntryDto request) throws BadRequestDetailsException;

  void updateBorrowEntryById(UpdateBorrowEntryDto request) throws BadRequestDetailsException;

  void deleteBorrowEntryById(Long id);
}
