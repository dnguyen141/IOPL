package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.CreateBorrowEntryDto;
import inst.iop.LibraryManager.library.dtos.ListBorrowEntriesByBookIdAndStatusDto;
import inst.iop.LibraryManager.library.dtos.ListBorrowEntriesByStatusDto;
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

@RequiredArgsConstructor
@CrossOrigin
@RestController
public class BorrowEntryControllerImpl implements BorrowEntryController {

  private final BorrowEntryService borrowEntryService;
  private final ApiResponseEntityFactory responseEntityFactory;

  /**
   * The API end-point for getting a borrow entry
   *
   * @param id borrow entry's id
   * @return ResponseEntity that contains a report message, http response code - 200 if success or 400 if error, and
   * an object contains the borrow entry's details
   */
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

  /**
   * The API end-point for getting a list of borrow entries based on borrow status with pagination
   *
   * @param status status of borrow entries
   * @param pageNumber page index
   * @param pageSize number of entries in a page
   * @return ResponseEntity that contains a report message, http response code - 200 if success or 400 if error, and
   * an object contains the borrow entries details
   */
  @Override
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
  public ResponseEntity<Object> listAllBorrowEntriesByStatus(String status, Integer pageNumber, Integer pageSize) {
    Page<BorrowEntry> borrowEntries = borrowEntryService.listBorrowEntriesByStatus(
        ListBorrowEntriesByStatusDto.builder()
            .status(status)
            .pageNumber(pageNumber)
            .pageSize(pageSize)
            .build()
    );
    return findBorrowEntriesResultConstructor(borrowEntries);
  }

  /**
   * The API end-point for getting a list of borrow entries from current user based on borrow status with pagination
   *
   * @param status status of borrow entries
   * @param pageNumber page index
   * @param pageSize number of entries in a page
   * @return ResponseEntity that contains a report message, http response code - 200 if success or 400 if error, and
   * an object contains the borrow entries details
   */
  @Override
  public ResponseEntity<Object> listBorrowEntriesFromCurrentUserByStatus(String status, Integer pageNumber,
                                                                         Integer pageSize) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Page<BorrowEntry> borrowEntries = borrowEntryService.listBorrowEntriesByUsernameAndStatus(
        authentication.getName(),
        ListBorrowEntriesByStatusDto.builder()
            .status(status)
            .pageNumber(pageNumber)
            .pageSize(pageSize)
            .build()
    );
    return findBorrowEntriesResultConstructor(borrowEntries);
  }

  /**
   * The API end-point for getting a list of borrow entries based on book id and borrow status with pagination
   *
   * @param bookId id of the book
   * @param status status of borrow entries
   * @param pageNumber page index
   * @param pageSize number of entries in a page
   * @return ResponseEntity that contains a report message, http response code - 200 if success or 400 if error, and
   * an object contains the borrow entries details
   */
  @Override
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
  public ResponseEntity<Object> listBorrowEntriesByBookIdAndStatus(Long bookId, String status, Integer pageNumber,
                                                                   Integer pageSize) {
    Page<BorrowEntry> borrowEntries = borrowEntryService.listBorrowEntriesByBookIdAndStatus(
        ListBorrowEntriesByBookIdAndStatusDto.builder()
            .bookId(bookId)
            .status(status)
            .pageNumber(pageNumber)
            .pageSize(pageSize)
            .build()
    );
    return findBorrowEntriesResultConstructor(borrowEntries);
  }

  /**
   * The API end-point for checking book's availability
   *
   * @param bookId book's id
   * @return ResponseEntity that contains a report message, http response code - 200 if success or 400 if error, and
   * an object contains the borrow entry's availability
   */
  @Override
  public ResponseEntity<Object> getBookAvailability(Long bookId) {
    Map<String, Object> details = new HashMap<>();
    details.put("available", borrowEntryService.getBookAvailability(bookId));

    return responseEntityFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully get book availability", details
    );
  }

  /**
   * The API end-point for creating a new borrow entry
   *
   * @param request contains information for new borrow entry
   * @return ResponseEntity that contains a report message and http response code - 201 if success or 400 if error
   */
  @Override
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
  public ResponseEntity<Object> createBorrowEntry(CreateBorrowEntryDto request) {
    borrowEntryService.createBorrowEntry(request);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.CREATED, "Successfully create new borrow entry"
    );
  }

  /**
   * The API end-point for creating a new requested borrow entry
   *
   * @param request contains information for new borrow entry
   * @return ResponseEntity that contains a report message and http response code - 201 if success or 400 if error
   */
  @Override
  @PreAuthorize("hasRole('ROLE_USER')")
  public ResponseEntity<Object> createRequestedBorrowEntry(CreateBorrowEntryDto request) {
    borrowEntryService.createBorrowEntry(request);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.CREATED, "Successfully request new borrow entry"
    );
  }

  /**
   * The API end-point for updating a borrow entry
   *
   * @param request contains updated information for borrow entry
   * @return ResponseEntity that contains a report message and http response code - 202 if success or 400 if error
   */
  @Override
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
  public ResponseEntity<Object> updateBorrowEntryById(UpdateBorrowEntryDto request) {
    borrowEntryService.updateBorrowEntryById(request);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.ACCEPTED, "Successfully update borrow entry"
    );
  }

  /**
   * The API end-point for deleting a borrow entry with whatever status
   *
   * @param id borrow entry's id
   * @return ResponseEntity that contains a report message and http response code - 200 if success or 400 if error
   */
  @Override
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
  public ResponseEntity<Object> deleteBorrowEntryById(Long id) {
    borrowEntryService.deleteBorrowEntryById(id);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.NO_CONTENT, "Successfully delete borrow entry"
    );
  }

  /**
   * The API end-point for user to delete a borrow entry that was requested from them
   *
   * @param id borrow entry's id
   * @return ResponseEntity that contains a report message and http response code - 200 if success or 400 if error
   */
  @Override
  public ResponseEntity<Object> deleteRequestedBorrowEntryById(Long id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    BorrowEntry borrowEntry = borrowEntryService.getBorrowEntryById(id);
    if (borrowEntry.getStatus().equals(BorrowStatus.Requested)
        && borrowEntry.getUser().getEmail().equals(authentication.getName())) {
      borrowEntryService.deleteBorrowEntryById(id);
      return responseEntityFactory.createSuccessResponse(
          HttpStatus.NO_CONTENT, "Successfully delete borrow entry"
      );
    }

    Map<String, String> details = new HashMap<>();
    details.put("authorization", "You are not allowed to delete the borrow entry or the borrow entry is not exists");
    return responseEntityFactory.createErrorWithDetailsResponse(
        HttpStatus.UNAUTHORIZED, "Invalid delete borrow entry request", details
    );
  }

  /**
   * Helper function to construct borrow entries to a proper response body
   *
   * @param borrowEntries a page of borrow entries
   * @return ResponseEntity that contains a report message and http response code 200
   */
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
