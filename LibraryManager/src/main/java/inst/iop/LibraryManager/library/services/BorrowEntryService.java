package inst.iop.LibraryManager.library.services;

import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.library.dtos.CreateBorrowEntryDto;
import inst.iop.LibraryManager.library.dtos.UpdateBorrowEntryDto;
import inst.iop.LibraryManager.library.entities.Book;
import inst.iop.LibraryManager.library.entities.BorrowEntry;
import inst.iop.LibraryManager.library.entities.enums.BorrowStatus;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface BorrowEntryService {

  Optional<BorrowEntry> getBorrowEntryById(Long id);

  List<BorrowEntry> listBorrowEntriesByStatus(BorrowStatus status);

  List<BorrowEntry> listBorrowEntriesByUserIdAndStatus(User user, BorrowStatus status) throws NoSuchElementException;

  List<BorrowEntry> listBorrowEntriesByBookIdAndStatus(Book book, String status);

  void createBorrowEntry(CreateBorrowEntryDto borrowEntryDto);

  void updateBorrowEntryById(Long id, UpdateBorrowEntryDto borrowEntryDto) throws IllegalArgumentException;

  void deleteBorrowEntryById(Long id) throws IllegalArgumentException;
}
