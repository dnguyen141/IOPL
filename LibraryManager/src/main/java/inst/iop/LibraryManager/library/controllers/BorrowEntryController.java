package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.CreateBorrowEntryDto;
import inst.iop.LibraryManager.library.dtos.UpdateBorrowEntryDto;
import inst.iop.LibraryManager.library.entities.constraints.BorrowStatusConstraint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/issues")
@Tag(name = "borrow-entry-controller")
@Validated
public interface BorrowEntryController {

  @Operation(summary = "Get borrow entry by their id. Only for admins and moderators.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully get borrow entry",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to get borrow entry",
          content = @Content)
  })
  @GetMapping("/{id}")
  ResponseEntity<Object> getBorrowEntryById(@Parameter(description = "id of the borrow entry")
                                            @PathVariable Long id);

  @Operation(summary = "List borrow entries by status. Only for admins and moderators.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully list borrow entries",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to list borrow entries",
          content = @Content)
  })
  @GetMapping("")
  ResponseEntity<Object> listAllBorrowEntriesByStatus(
      @Parameter(description = "valid status of a borrow entry, " +
          "can only be Requested, Issued, Returned, Overdue or Lost.") @RequestParam String status,
      @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer pageNumber,
      @Parameter(description = "Number of entries in a page") @RequestParam(defaultValue = "20") Integer pageSize
  );

  @Operation(summary = "List borrow entries from current user by status. Only for logged-in users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully list borrow entries",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to list borrow entries",
          content = @Content)
  })
  @GetMapping("/list")
  ResponseEntity<Object> listBorrowEntriesFromCurrentUserByStatus(
      @Parameter(description = "valid status of a borrow entry, " +
          "can only be Requested, Issued, Returned, Overdue or Lost.") @RequestParam String status,
      @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer pageNumber,
      @Parameter(description = "Number of entries in a page") @RequestParam(defaultValue = "20") Integer pageSize
  );

  @Operation(summary = "List borrow entries related to a specific book by status. Only for admins and moderators.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully list borrow entries",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to list borrow entries",
          content = @Content)
  })
  @GetMapping("/list-by-book")
  ResponseEntity<Object> listBorrowEntriesByBookIdAndStatus(
      @Parameter(description = "id of a book") @RequestParam Long bookId,
      @Parameter(description = "valid status of a borrow entry, can only be " +
          "Requested, Issued, Returned, Overdue or Lost.") @RequestParam @BorrowStatusConstraint String status,
      @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer pageNumber,
      @Parameter(description = "Number of entries in a page") @RequestParam(defaultValue = "20") Integer pageSize
  );

  @Operation(summary = "Get number of available copies of a book. Only for logged-in users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully get book availability",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to get book availability",
          content = @Content)
  })
  @GetMapping("/availability")
  ResponseEntity<Object> getBookAvailability(@Parameter(description = "id of the requested book")
                                             @RequestParam Long bookId);

  @Operation(summary = "Create a borrow entry. Only for moderators and admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully create a borrow entry",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to create a borrow entry",
          content = @Content)
  })
  @PostMapping("/create")
  ResponseEntity<Object> createBorrowEntry(@Parameter(description = "information to create borrow entry")
                                           @RequestBody CreateBorrowEntryDto request);

  @Operation(summary = "Request a borrow entry. Only for logged-in users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully request a borrow entry",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to request a borrow entry",
          content = @Content)
  })
  @PostMapping("/request")
  ResponseEntity<Object> createRequestedBorrowEntry(
      @Parameter(description = "information to create requested borrow entry")
      @RequestBody CreateBorrowEntryDto request
  );

  @Operation(summary = "Update a borrow entry. Only for moderators and admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully update a borrow entry",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to update a borrow entry",
          content = @Content)
  })
  @PutMapping("/update/{id}")
  ResponseEntity<Object> updateBorrowEntryById(@Parameter(description = "id of the borrow entry")
                                               @PathVariable Long id,
                                               @Parameter(description = "updated information for the borrow entry")
                                               @RequestBody UpdateBorrowEntryDto request);

  @Operation(summary = "Delete a borrow entry. Only for moderators and admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Successfully delete a borrow entry",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to delete a borrow entry",
          content = @Content)
  })
  @DeleteMapping("/delete/{id}")
  ResponseEntity<Object> deleteBorrowEntryById(@Parameter(description = "id of the borrow entry that will be deleted")
                                               @PathVariable Long id);

  @Operation(summary = "Delete a borrow entry. Only for users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Successfully delete a requested borrow entry",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to delete a requested borrow entry",
          content = @Content)
  })
  @DeleteMapping("/delete-requested/{id}")
  ResponseEntity<Object> deleteRequestedBorrowEntryById(
      @Parameter(description = "id of the borrow entry that will be deleted")
      @PathVariable Long id
  );
}
