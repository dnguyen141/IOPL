package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.CreateBorrowEntryDto;
import inst.iop.LibraryManager.library.dtos.UpdateBorrowEntryDto;
import inst.iop.LibraryManager.library.entities.constraints.BorrowStatusConstraint;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/issues")
@Validated
public interface BorrowEntryController {

  /**
   * The API end-point for getting a borrow entry
   *
   * @param id borrow entry's id
   * @return ResponseEntity that contains a report message, http response code - 200 if success or 400 if error, and
   * an object contains the borrow entry's details
   */
  @GetMapping("/{id}")
  ResponseEntity<Object> getBorrowEntryById(@PathVariable Long id);

  /**
   * The API end-point for getting a list of borrow entries based on borrow status with pagination
   *
   * @param status borrow entry's status. Can only be Requested, Issued, Returned, Overdue or Lost.
   * @param pageNumber page index
   * @param pageSize number of borrow entries in a page
   * @return ResponseEntity that contains a report message, http response code - 200 if success or 400 if error, and
   * an object contains the borrow entries details
   */
  @GetMapping("")
  ResponseEntity<Object> listAllBorrowEntriesByStatus(@RequestParam String status,
                                                      @RequestParam(defaultValue = "0") Integer pageNumber,
                                                      @RequestParam(defaultValue = "20") Integer pageSize);

  /**
   * The API end-point for getting a list of borrow entries from current user based on borrow status with pagination
   *
   * @param status borrow entry's status. Can only be Requested, Issued, Returned, Overdue or Lost.
   * @param pageNumber page index
   * @param pageSize number of borrow entries in a page
   * @return ResponseEntity that contains a report message, http response code - 200 if success or 400 if error, and
   * an object contains the borrow entries details
   */
  @GetMapping("/list")
  ResponseEntity<Object> listBorrowEntriesFromCurrentUserByStatus(@RequestParam String status,
                                                                  @RequestParam(defaultValue = "0") Integer pageNumber,
                                                                  @RequestParam(defaultValue = "20") Integer pageSize
  );

  /**
   * The API end-point for getting a list of borrow entries based on book id and borrow status with pagination
   *
   * @param bookId book's id
   * @param status borrow entry's status. Can only be Requested, Issued, Returned, Overdue or Lost.
   * @param pageNumber page index
   * @param pageSize number of borrow entries in a page
   * @return ResponseEntity that contains a report message, http response code - 200 if success or 400 if error, and
   * an object contains the borrow entries details
   */
  @GetMapping("/list-by-book")
  ResponseEntity<Object> listBorrowEntriesByBookIdAndStatus(@RequestParam Long bookId,
                                                            @RequestParam @BorrowStatusConstraint String status,
                                                            @RequestParam(defaultValue = "0") Integer pageNumber,
                                                            @RequestParam(defaultValue = "20") Integer pageSize);

  /**
   * The API end-point for checking book's availability
   *
   * @param bookId book's id
   * @return ResponseEntity that contains a report message, http response code - 200 if success or 400 if error, and
   * an object contains the borrow entry's availability
   */
  @GetMapping("/availability")
  ResponseEntity<Object> getBookAvailability(@RequestParam Long bookId);

  /**
   * The API end-point for creating a new borrow entry
   *
   * @param request contains information for new borrow entry
   * @return ResponseEntity that contains a report message and http response code - 200 if success or 400 if error
   */
  @PostMapping("/create")
  ResponseEntity<Object> createBorrowEntry(@RequestBody CreateBorrowEntryDto request);

  /**
   * The API end-point for updating a borrow entry
   *
   * @param id borrow entry's id
   * @param request contains updated information for borrow entry
   * @return ResponseEntity that contains a report message and http response code - 200 if success or 400 if error
   */
  @PutMapping("/update/{id}")
  ResponseEntity<Object> updateBorrowEntryById(@PathVariable Long id, @RequestBody UpdateBorrowEntryDto request);

  /**
   * The API end-point for deleting a borrow entry with whatever status
   *
   * @param id borrow entry's id
   * @return ResponseEntity that contains a report message and http response code - 200 if success or 400 if error
   */
  @DeleteMapping("/delete/{id}")
  ResponseEntity<Object> deleteBorrowEntryById(@PathVariable Long id);

  /**
   * The API end-point for user to delete a borrow entry that was requested from them
   *
   * @param id borrow entry's id
   * @return ResponseEntity that contains a report message and http response code - 200 if success or 400 if error
   */
  @DeleteMapping("/delete-requested/{id}")
  ResponseEntity<Object> deleteRequestedBorrowEntryById(@PathVariable Long id);
}
