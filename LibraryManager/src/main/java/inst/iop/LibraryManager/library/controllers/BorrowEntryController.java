package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.CreateBorrowEntryDto;
import inst.iop.LibraryManager.library.dtos.UpdateBorrowEntryDto;
import inst.iop.LibraryManager.library.entities.constraints.BorrowStatusConstraint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/issues")
public interface BorrowEntryController {

  @GetMapping("/{id}")
  ResponseEntity<Object> getBorrowEntryById(@PathVariable Long id);

  @GetMapping("")
  ResponseEntity<Object> listAllBorrowEntriesByStatus(@RequestParam @BorrowStatusConstraint String status,
                                                      @RequestParam(defaultValue = "0") Integer pageNumber,
                                                      @RequestParam(defaultValue = "20") Integer pageSize);

  @GetMapping("/list")
  ResponseEntity<Object> listBorrowEntriesFromCurrentUserByStatus(@RequestParam @BorrowStatusConstraint String status,
                                                                  @RequestParam(defaultValue = "0") Integer pageNumber,
                                                                  @RequestParam(defaultValue = "20") Integer pageSize
  );

  @GetMapping("/list-by-book")
  ResponseEntity<Object> listBorrowEntriesByBookIdAndStatus(@RequestParam Long bookId,
                                                            @RequestParam @BorrowStatusConstraint String status,
                                                            @RequestParam(defaultValue = "0") Integer pageNumber,
                                                            @RequestParam(defaultValue = "20") Integer pageSize);

  @GetMapping("/availability")
  ResponseEntity<Object> getBookAvailability(@RequestParam Long bookId);

  @PostMapping("/create")
  ResponseEntity<Object> createBorrowEntry(@RequestBody CreateBorrowEntryDto borrowEntryDto);

  @PostMapping("/update/{id}")
  ResponseEntity<Object> updateBorrowEntryById(@PathVariable Long id, @RequestBody UpdateBorrowEntryDto borrowEntryDto);

  @DeleteMapping("/delete/{id}")
  ResponseEntity<Object> deleteBorrowEntryById(@PathVariable Long id);

  @DeleteMapping("/delete-requested/{id}")
  ResponseEntity<Object> deleteRequestedBorrowEntryById(@PathVariable Long id);
}
