package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.authentication.services.UserService;
import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.library.dtos.CreateBorrowEntryDto;
import inst.iop.LibraryManager.library.entities.Book;
import inst.iop.LibraryManager.library.entities.BorrowEntry;
import inst.iop.LibraryManager.library.entities.enums.BorrowStatus;
import inst.iop.LibraryManager.library.services.BookService;
import inst.iop.LibraryManager.library.services.BorrowEntryService;
import inst.iop.LibraryManager.utilities.responses.InformationApiResponse;
import inst.iop.LibraryManager.utilities.responses.SuccessWithDataApiResponse;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@RestController
public class BorrowEntryControllerImpl implements BorrowEntryController {

  private final BorrowEntryService borrowEntryService;
  private final BookService bookService;
  private final UserService userService;

  @Override
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
  public ResponseEntity<?> getBorrowEntryById(@PathVariable Long id) {
    Optional<BorrowEntry> borrowEntry = borrowEntryService.getBorrowEntryById(id);
    if (borrowEntry.isPresent()) {
      Map<String, Object> details = new HashMap<>();
      details.put("borrow_entry", borrowEntry);

      return ResponseEntity.ok(
          new SuccessWithDataApiResponse(
              "success",
              200,
              "Successfully list all borrow entries",
              details
          )
      );
    }

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(new InformationApiResponse(
            "error",
            403,
            "Borrow entry with id " + id + " is not existed"
        ));
  }

  @GetMapping("/list-by-status")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
  public ResponseEntity<?> listBorrowEntriesByStatus(@RequestParam String status) {
    List<BorrowEntry> borrowEntries = borrowEntryService.listBorrowEntriesByStatus(BorrowStatus.valueOf(status));
    Map<String, Object> details = new HashMap<>();
    details.put("borrow_entries", borrowEntries);

    return ResponseEntity.ok(
        new SuccessWithDataApiResponse(
            "success",
            200,
            "Successfully list all borrow entries",
            details
        )
    );
  }

  @GetMapping("/list-by-user")
  public ResponseEntity<?> listBorrowEntriesByUserIdAndStatus(
      @RequestParam long userId,
      @RequestParam String status
  ) {
    Optional<BorrowStatus> bs = BorrowStatus.getBorrowStatusFromString(status);
    if (bs.isEmpty()) {
      return ResponseEntity
          .badRequest()
          .body(
              new InformationApiResponse(
                  "error",
                  400,
                  "Invalid borrow entry value"
              )
          );
    }

    User user = userService.findUserById(userId);
    List<BorrowEntry> borrowEntries = borrowEntryService.listBorrowEntriesByUserIdAndStatus(user, bs.get());
    Map<String, Object> details = new HashMap<>();
    details.put("borrow_entries", borrowEntries);

    return ResponseEntity.ok(new SuccessWithDataApiResponse(
        "success",
        200,
        "Successfully get borrow entries details using user id and status",
        details
    ));
  }

  @GetMapping("/list/book")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
  public ResponseEntity<?> listBorrowEntriesByBookId(
      @RequestParam Long bookId,
      @RequestParam String status
  ) {
    Optional<Book> book = bookService.getBookFromId(bookId);
    if (book.isEmpty()) {
      return ResponseEntity
          .badRequest()
          .body(
              new InformationApiResponse(
                  "error",
                  400,
                  "Book with the id " + bookId + " can't be found"
              )
          );
    }

    List<BorrowEntry> borrowEntries = borrowEntryService.listBorrowEntriesByBookIdAndStatus(book.get(), status);
    Map<String, Object> details = new HashMap<>();
    details.put("borrow_entries", borrowEntries);

    return ResponseEntity.ok(new SuccessWithDataApiResponse(
        "success",
        200,
        "Successfully get borrow entries details using user id",
        details
    ));
  }

  @GetMapping("/list/current_user")
  public ResponseEntity<?> listBorrowEntriesFromCurrentUser(@RequestParam String status) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user;
    try {
      user = userService.findUserByEmail(authentication.getName());
    } catch (BadRequestDetailsException e) {
      return ResponseEntity
          .badRequest()
          .body(
              new SuccessWithDataApiResponse(
                  "error",
                  400,
                  "Unable to list borrow entries from current user",
                  e.getViolations()
              )
          );
    }

    List<BorrowEntry> borrowEntries = borrowEntryService.listBorrowEntriesByUserIdAndStatus(user,
        BorrowStatus.valueOf(status));
    Map<String, Object> details = new HashMap<>();
    details.put("borrow_entries", borrowEntries);

    return ResponseEntity.ok(new SuccessWithDataApiResponse(
        "success",
        200,
        "Successfully get borrow entries details using user id",
        details
    ));
  }

  @PostMapping("/create")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
  public ResponseEntity<?> createBorrowEntry(@RequestParam @Valid CreateBorrowEntryDto borrowEntryDto,
                                             BindingResult bindingResult) {
    User user = userService.findUserById(borrowEntryDto.getUserId());
    Optional<Book> book = bookService.getBookFromId(borrowEntryDto.getBookId());

    Map<String, Object> violations = new HashMap<>();
    if (user != null) {
      violations.put("user", "User with id " + borrowEntryDto.getUserId() + " can't be found");
    }

    if (book.isEmpty()) {
      violations.put("book", "Book with id " + borrowEntryDto.getBookId() + " can't be found");
    }

    if (bindingResult.hasErrors()) {
      violations.put("borrowStatus", "Invalid borrow status with value " + borrowEntryDto.getStatus());
    }

    if (!violations.isEmpty()) {
      return ResponseEntity
          .badRequest()
          .body(
              new SuccessWithDataApiResponse(
                  "error",
                  400,
                  "Invalid create borrow entry request",
                  violations
              )
          );
    }

    borrowEntryService.createBorrowEntry(borrowEntryDto);
    return ResponseEntity.ok(new InformationApiResponse(
        "success",
        200,
        "Successfully create borrow entry"
    ));
  }

//  @PostMapping("/update/{id}")
//  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
//  public ResponseEntity<?> updateBorrowEntryById(@PathVariable Long id,
//                                                 @RequestBody @Valid UpdateBorrowEntryDto borrowEntryDto,
//                                                 BindingResult bindingResult) {
//    Map<String, Object> violations = handleBindingResult(bindingResult);
//    Optional<BorrowEntry> borrowEntry = borrowEntryService.getBorrowEntryById(id);
//    if (borrowEntry.isEmpty()) {
//      violations.put("borrowEntry", "Borrow entry is not exists");
//    }
//
//    if (!violations.isEmpty()) {
//      return ResponseEntity
//          .badRequest()
//          .body(
//              new InformationWithDetailsResponse(
//                  "error",
//                  400,
//                  "Invalid create borrow entry request",
//                  violations
//              )
//          );
//    }
//
//    borrowEntryService.updateBorrowEntryById(id, borrowEntryDto.getUser(), borrowEntryDto.getBook(),
//        borrowEntryDto.getBorrowStatus());
//    return ResponseEntity.ok(new InformationResponse(
//        "success",
//        200,
//        "Successfully update borrow entry"
//    ));
//  }

  @DeleteMapping("/delete/{id}")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
  public ResponseEntity<?> deleteBorrowEntryById(@PathVariable Long id) {
    Optional<BorrowEntry> borrowEntry = borrowEntryService.getBorrowEntryById(id);

    if (borrowEntry.isEmpty()) {
      Map<String, Object> violations = new HashMap<>();
      violations.put("borrowEntry", "Borrow entry is not exists");
      return ResponseEntity
          .badRequest()
          .body(
              new SuccessWithDataApiResponse(
                  "error",
                  400,
                  "Invalid delete borrow entry request",
                  violations
              )
          );
    }

    borrowEntryService.deleteBorrowEntryById(id);
    return ResponseEntity.ok(new InformationApiResponse(
        "success",
        200,
        "Successfully delete borrow entry"
    ));
  }

  @DeleteMapping("/delete")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_USER')")
  public ResponseEntity<?> deleteRequestBorrowEntryById(@RequestParam Long id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Optional<BorrowEntry> borrowEntry = borrowEntryService.getBorrowEntryById(id);

    if (borrowEntry.isEmpty()
        || !authentication.getName().equals(borrowEntry.get().getUser().getEmail())
        || !borrowEntry.get().getStatus().name().equals(BorrowStatus.Requested.name())) {
      Map<String, Object> violations = new HashMap<>();
      violations.put("borrowEntry", "Borrow entry is not exists or in a valid state");
      return ResponseEntity
          .badRequest()
          .body(
              new SuccessWithDataApiResponse(
                  "error",
                  400,
                  "Invalid delete borrow entry request",
                  violations
              )
          );
    }

    borrowEntryService.deleteBorrowEntryById(id);
    return ResponseEntity.ok(new InformationApiResponse(
        "success",
        200,
        "Successfully delete borrow entry"
    ));
  }
}
