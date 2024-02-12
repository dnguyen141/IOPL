package inst.iop.LibraryManager.library.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/v1/borrow")
public interface BorrowEntryController {

  @GetMapping("/{id}")
  ResponseEntity<?> getBorrowEntryById(@PathVariable Long id);

  @GetMapping("/list")
  ResponseEntity<?> listBorrowEntriesByStatus(@RequestParam String status);
}
