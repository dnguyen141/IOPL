package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.CreateUpdateTypeDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/types")
@Tag(name = "book-type-controller")
@Validated
public interface BookTypeController {

  @Operation(summary = "List all book types. Only for moderators and admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully list book types",
          content = @Content)
  })
  @GetMapping("")
  ResponseEntity<Object> listAllBookTypes();

  @Operation(summary = "Get book type from its id. Only for moderators and admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully get book type",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to get book type",
          content = @Content)
  })
  @GetMapping("/{id}")
  ResponseEntity<Object> getBookTypeById(@Parameter(description = "id of the book type that will be updated")
                                         @PathVariable Long id);

  @Operation(summary = "Create a new book type. Only for moderators and admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully create book type",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to create book type",
          content = @Content)
  })
  @PostMapping("/create")
  ResponseEntity<Object> createBookType(@Parameter(description = "name of the new book type")
                                        @RequestBody CreateUpdateTypeDto request);

  @Operation(summary = "Update a book type by its id. Only for moderators and admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202", description = "Successfully update book type",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to update book type",
          content = @Content)
  })
  @PutMapping("/edit/{id}")
  ResponseEntity<Object> updateBookType(@Parameter(description = "id of the book type that will be updated")
                                        @PathVariable Long id,
                                        @Parameter(description = "updated name of the book type")
                                        @RequestBody CreateUpdateTypeDto request);

  @Operation(summary = "Delete a book type by its id. Only for moderators and admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Successfully delete book type",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to delete book type",
          content = @Content)
  })
  @DeleteMapping("/delete/{id}")
  ResponseEntity<Object> deleteBookType(@Parameter(description = "id of the book type that will be deleted")
                                        @PathVariable Long id);
}
