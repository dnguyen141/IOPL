package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.CreateBorrowEntryDto;
import inst.iop.LibraryManager.library.dtos.UpdateBorrowEntryDto;
import inst.iop.LibraryManager.library.entities.BorrowEntry;
import inst.iop.LibraryManager.library.entities.enums.BorrowStatus;
import inst.iop.LibraryManager.library.services.BorrowEntryService;
import inst.iop.LibraryManager.utilities.responses.ApiResponseEntityFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@CrossOrigin
@RestController
public class BorrowEntryControllerImpl implements BorrowEntryController {

  private final BorrowEntryService borrowEntryService;
  private final ApiResponseEntityFactory responseEntityFactory;

  @Override
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
  public ResponseEntity<Object> getBorrowEntryById(Long id) {
    BorrowEntry borrowEntry = borrowEntryService.getBorrowEntryById(id);
    Map<String, Object> details = new HashMap<>();
    details.put("borrow-entry", borrowEntry.mapToDto());
    return responseEntityFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully get borrow entry by id", details
    );
  }

  @Override
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
  public ResponseEntity<Object> listAllBorrowEntriesByStatus(String status, Integer pageNumber, Integer pageSize) {
    Page<BorrowEntry> borrowEntries = borrowEntryService.listBorrowEntriesByStatus(status, pageNumber, pageSize);
    return findBorrowEntriesResultConstructor(borrowEntries);
  }

  @Override
  public ResponseEntity<Object> listBorrowEntriesFromCurrentUserByStatus(String status, Integer pageNumber,
                                                                         Integer pageSize) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Page<BorrowEntry> borrowEntries = borrowEntryService.listBorrowEntriesByUsernameAndStatus(
        authentication.getName(), status, pageNumber, pageSize
    );
    return findBorrowEntriesResultConstructor(borrowEntries);
  }

  @Override
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
  public ResponseEntity<Object> listBorrowEntriesByBookIdAndStatus(Long bookId, String status, Integer pageNumber,
                                                                   Integer pageSize) {
    Page<BorrowEntry> borrowEntries = borrowEntryService.listBorrowEntriesByBookIdAndStatus(bookId, status, pageNumber,
        pageSize);
    return findBorrowEntriesResultConstructor(borrowEntries);
  }

  @Override
  public ResponseEntity<Object> getBookAvailability(Long bookId) {
    Map<String, Object> details = new HashMap<>();
    details.put("available", borrowEntryService.getBookAvailable(bookId));

    return responseEntityFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully get book availability", details
    );
  }

  @Override
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
  public ResponseEntity<Object> createBorrowEntry(CreateBorrowEntryDto borrowEntryDto) {
    borrowEntryService.createBorrowEntry(borrowEntryDto);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.CREATED, "Successfully create new borrow entry"
    );
  }

  @Override
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
  public ResponseEntity<Object> updateBorrowEntryById(Long id, UpdateBorrowEntryDto borrowEntryDto) {
    borrowEntryService.updateBorrowEntryById(id, borrowEntryDto);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.ACCEPTED, "Successfully update borrow entry"
    );
  }

  @Override
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
  public ResponseEntity<Object> deleteBorrowEntryById(Long id) {
    borrowEntryService.deleteBorrowEntryById(id);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.OK, "Successfully delete borrow entry"
    );
  }

  @Override
  public ResponseEntity<Object> deleteRequestedBorrowEntryById(Long id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    BorrowEntry borrowEntry = borrowEntryService.getBorrowEntryById(id);
    if (borrowEntry.getStatus().equals(BorrowStatus.Requested)
        && borrowEntry.getUser().getEmail().equals(authentication.getName())) {
      borrowEntryService.deleteBorrowEntryById(id);
      return responseEntityFactory.createSuccessResponse(
          HttpStatus.OK, "Successfully delete borrow entry"
      );
    }

    Map<String, String> details = new HashMap<>();
    details.put("authorization", "You are not allowed to delete the borrow entry or the borrow entry is not exists");
    return responseEntityFactory.createErrorWithDetailsResponse(
        HttpStatus.UNAUTHORIZED, "Invalid delete borrow entry request", details
    );
  }

  private ResponseEntity<Object> findBorrowEntriesResultConstructor(Page<BorrowEntry> borrowEntries) {
    Map<String, Object> details = new HashMap<>();
    details.put("borrow-entries", borrowEntries.stream()
        .sorted(Comparator.comparingLong(BorrowEntry::getId))
        .map(BorrowEntry::mapToDto)
    );
    details.put("pageNumber", borrowEntries.getNumber());
    details.put("pageSize", borrowEntries.getSize());
    details.put("numberOfPages", borrowEntries.getTotalPages());
    return responseEntityFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully list borrow entries", details
    );
  }
}
