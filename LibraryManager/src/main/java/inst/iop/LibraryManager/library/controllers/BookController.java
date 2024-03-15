package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.CreateBookDto;
import inst.iop.LibraryManager.library.dtos.UpdateBookDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/v1/books")
@Tag(name = "book-controller")
public interface BookController {

  @Operation(summary = "List all books and their information. Only for logged-in users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully list books information",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to list books information",
          content = @Content)
  })
  @GetMapping("")
  ResponseEntity<Object> listAllBooks(@Parameter(description = "Page number")
                                      @RequestParam(defaultValue = "0") Integer pageNumber,
                                      @Parameter(description = "Number of entries in a page")
                                      @RequestParam(defaultValue = "10") Integer pageSize);

  @Operation(summary = "Get book information from its id. Only for logged-in users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully get book information",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to get book information",
          content = @Content)
  })
  @GetMapping("/{id}")
  ResponseEntity<Object> getBookById(@Parameter(description = "id of the book that will be retrieved")
                                     @PathVariable Long id);

  @Operation(summary = "Search book using an input search query from user. Only for logged-in users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully search books",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to search books",
          content = @Content)
  })
  @GetMapping("/search")
  ResponseEntity<Object> findBooks(
      @Parameter(description = "search term") @RequestParam String term,
      @Parameter(description = "filter books that have been published before specific year")
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @Parameter(description = "filter books that have been published after specific year")
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @Parameter(description = "Page number")
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @Parameter(description = "Number of entries in a page")
      @RequestParam(defaultValue = "10") Integer pageSize
  );

  @Operation(summary = "Search book by title using an input search query from user. Only for logged-in users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully search books",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to search books",
          content = @Content)
  })
  @GetMapping("/search/title")
  ResponseEntity<Object> findBooksByTitle(
      @Parameter(description = "search term") @RequestParam String term,
      @Parameter(description = "filter books that have been published before specific year")
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @Parameter(description = "filter books that have been published after specific year")
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @Parameter(description = "Page number")
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @Parameter(description = "Number of entries in a page")
      @RequestParam(defaultValue = "10") Integer pageSize
  );

  @Operation(summary = "Search book by authors using an input search query from user. Only for logged-in users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully search books",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to search books",
          content = @Content)
  })
  @GetMapping("/search/authors")
  ResponseEntity<Object> findBooksByAuthors(
      @Parameter(description = "search term") @RequestParam String term,
      @Parameter(description = "filter books that have been published before specific year")
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @Parameter(description = "filter books that have been published after specific year")
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @Parameter(description = "Page number")
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @Parameter(description = "Number of entries in a page")
      @RequestParam(defaultValue = "10") Integer pageSize
  );

  @Operation(summary = "Search book by publisher using an input search query from user. Only for logged-in users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully search books",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to search books",
          content = @Content)
  })
  @GetMapping("/search/publisher")
  ResponseEntity<Object> findBooksByPublisher(
      @Parameter(description = "search term") @RequestParam String term,
      @Parameter(description = "filter books that have been published before specific year")
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @Parameter(description = "filter books that have been published after specific year")
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @Parameter(description = "Page number")
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @Parameter(description = "Number of entries in a page")
      @RequestParam(defaultValue = "10") Integer pageSize
  );

  @Operation(summary = "Search book by book type using an input search query from user. Only for logged-in users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully search books",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to search books",
          content = @Content)
  })
  @GetMapping("/search/type")
  ResponseEntity<Object> findBooksByType(
      @Parameter(description = "search term") @RequestParam String term,
      @Parameter(description = "filter books that have been published before specific year")
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @Parameter(description = "filter books that have been published after specific year")
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @Parameter(description = "Page number")
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @Parameter(description = "Number of entries in a page")
      @RequestParam(defaultValue = "10") Integer pageSize
  );

  @Operation(summary = "Search book by book field using an input search query from user. Only for logged-in users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully search books",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to search books",
          content = @Content)
  })
  @GetMapping("/search/field")
  ResponseEntity<Object> findBooksByField(
      @Parameter(description = "search term") @RequestParam String term,
      @Parameter(description = "filter books that have been published before specific year")
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @Parameter(description = "filter books that have been published after specific year")
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @Parameter(description = "Page number")
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @Parameter(description = "Number of entries in a page")
      @RequestParam(defaultValue = "10") Integer pageSize
  );

  @Operation(summary = "Search book by book ISBN using an input search query from user. Only for logged-in users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully search books",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to search books",
          content = @Content)
  })
  @GetMapping("/search/isbn")
  ResponseEntity<Object> findBooksByIsbn(
      @Parameter(description = "search term") @RequestParam String term,
      @Parameter(description = "filter books that have been published before specific year")
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @Parameter(description = "filter books that have been published after specific year")
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @Parameter(description = "Page number")
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @Parameter(description = "Number of entries in a page")
      @RequestParam(defaultValue = "10") Integer pageSize
  );

  @Operation(summary = "Search book by inventory number using an input search query from user. " +
      "Only for logged-in users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully search books",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to search books",
          content = @Content)
  })
  @GetMapping("/search/inventory-number")
  ResponseEntity<Object> findBooksByInventoryNumber(
      @Parameter(description = "search term") @RequestParam String term,
      @Parameter(description = "filter books that have been published before specific year")
      @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer beforeYear,
      @Parameter(description = "filter books that have been published after specific year")
      @RequestParam(defaultValue = "1900") Integer afterYear,
      @Parameter(description = "Page number")
      @RequestParam(defaultValue = "0") Integer pageNumber,
      @Parameter(description = "Number of entries in a page")
      @RequestParam(defaultValue = "10") Integer pageSize
  );

  @Operation(summary = "Get cover image of a specific book using its id. Only for logged-in users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully get book's cover",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to get book's cover",
          content = @Content)
  })
  @GetMapping("/{id}/cover")
  ResponseEntity<?> getCoverImage(@Parameter(description = "id of the book that will be retrieved")
                                  @PathVariable Long id);

  @Operation(summary = "Create a book entry in the library. Only for moderators and admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully create book",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to create book",
          content = @Content)
  })
  @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  ResponseEntity<Object> createBook(@Parameter(description = "All information needed for the book entry")
                                    @RequestPart CreateBookDto request,
                                    @Parameter(description = "Book cover's image which is uploaded by user")
                                    @RequestPart(name = "coverImage", required = false) MultipartFile coverImage);

  @Operation(summary = "Update a book entry's information. Only for moderators and admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202", description = "Successfully update book",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to update book",
          content = @Content)
  })
  @PostMapping(value = "/update/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  ResponseEntity<Object> updateBookById(@Parameter(description = "id of the book that will be updated")
                                        @PathVariable Long id,
                                        @Parameter(description = "Updated information for the book entry")
                                        @RequestPart UpdateBookDto request,
                                        @Parameter(description = "Updated cover image for the book entry")
                                        @RequestPart(name = "coverImage", required = false) MultipartFile coverImage);

  @Operation(summary = "Delete a book entry. Only for moderators and admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Successfully delete book",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to delete book",
          content = @Content)
  })
  @DeleteMapping("/delete/{id}")
  ResponseEntity<Object> deleteBookById(@Parameter(description = "id of the book that will be deleted")
                                        @PathVariable Long id);

  @Operation(summary = "Import books from an excel file. Only for admins.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully import from excel file",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to import books from excel file",
          content = @Content)
  })
  @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  ResponseEntity<Object> importFromExcelFile(@Parameter(description = "excel file that contains books' information")
                                             @RequestPart(name = "excelFile") MultipartFile excelFile);
}
