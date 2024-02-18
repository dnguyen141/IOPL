package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.CreateUpdateTypeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/types")
@Validated
public interface BookTypeController {

  /**
   * The API end-point for listing all book types
   *
   * @return ResponseEntity that contains a list of book types and http response code - 200 if success
   */
  @GetMapping("")
  ResponseEntity<Object> listAllBookTypes();

  /**
   * The API end-point for getting a book type from id
   *
   * @param id book type's id
   * @return ResponseEntity that contains the book type's information and http response code - 200 if success or
   * 400 if error
   */
  @GetMapping("/{id}")
  ResponseEntity<Object> getBookTypeById(@PathVariable Long id);

  /**
   * The API end-point for creating a new book type from string
   *
   * @param request which contains new book type's name
   * @return ResponseEntity that contains a report message and http response code - 200 if success or 400 if error
   */
  @PostMapping("/create")
  ResponseEntity<Object> createBookType(@RequestBody CreateUpdateTypeDto request);

  /**
   * The API end-point for editing a book type
   *
   * @param id book type's id
   * @param request which contains book type's new name
   * @return ResponseEntity that contains a report message and http response code - 200 if success or 400 if error
   */
  @PutMapping("/edit/{id}")
  ResponseEntity<Object> updateBookType(@PathVariable Long id, @RequestBody CreateUpdateTypeDto request);

  /**
   * The API end-point for deleting a book type
   *
   * @param id book type's id
   * @return ResponseEntity that contains a report message and http response code - 200 if success or 400 if error
   */
  @DeleteMapping("/delete/{id}")
  ResponseEntity<Object> deleteBookType(@PathVariable Long id);
}
