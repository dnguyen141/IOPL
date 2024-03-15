package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.CreateUpdateFieldDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/fields")
@Tag(name = "book-field-controller")
public interface BookFieldController {

  @Operation(summary = "List all book fields. Only for moderators and admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully list book fields",
          content = @Content)
  })
  @GetMapping("")
  ResponseEntity<Object> listAllBookFields();

  @Operation(summary = "Get book field from its id. Only for moderators and admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully get book field",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to get book field",
          content = @Content)
  })
  @GetMapping("/{id}")
  ResponseEntity<Object> getBookFieldById(@Parameter(description = "id of the book field that will be updated")
                                          @PathVariable Long id);

  @Operation(summary = "Create a new book field. Only for moderators and admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully create book field",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to create book field",
          content = @Content)
  })
  @PostMapping("/create")
  ResponseEntity<Object> createBookField(@Parameter(description = "name of the new book field")
                                         @RequestBody CreateUpdateFieldDto request);

  @Operation(summary = "Update a book field by its id. Only for moderators and admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202", description = "Successfully update book field",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to update book field",
          content = @Content)
  })
  @PutMapping("/edit/{id}")
  ResponseEntity<Object> updateBookField(@Parameter(description = "id of the book field that will be updated")
                                         @PathVariable Long id,
                                         @Parameter(description = "updated name of the book field")
                                         @RequestBody CreateUpdateFieldDto request);

  @Operation(summary = "Delete a book field by its id. Only for moderators and admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Successfully delete book field",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to delete book field",
          content = @Content)
  })
  @DeleteMapping("/delete/{id}")
  ResponseEntity<Object> deleteBookField(@Parameter(description = "id of the book field that will be deleted")
                                         @PathVariable Long id);
}
