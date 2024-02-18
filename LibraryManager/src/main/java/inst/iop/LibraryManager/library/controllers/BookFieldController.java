package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.CreateUpdateFieldDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/fields")
@Validated
public interface BookFieldController {

  /**
   * The API end-point for listing all book fields
   *
   * @return ResponseEntity that contains a list of book fields and http response code - 200 if success
   */
  @GetMapping("")
  ResponseEntity<Object> listAllBookFields();

  /**
   * The API end-point for getting a book field from id
   *
   * @param id book field's id
   * @return ResponseEntity that contains the book field's information and http response code - 200 if success or
   * 400 if error
   */
  @GetMapping("/{id}")
  ResponseEntity<Object> getBookFieldById(@PathVariable Long id);

  /**
   * The API end-point for creating a new book field from string
   *
   * @param request which contains new book field's name
   * @return ResponseEntity that contains a report message and http response code - 200 if success or 400 if error
   */
  @PostMapping("/create")
  ResponseEntity<Object> createBookField(@RequestBody CreateUpdateFieldDto request);

  /**
   * The API end-point for editing a book field
   *
   * @param id book field's id
   * @param request which contains book field's new name
   * @return ResponseEntity that contains a report message and http response code - 200 if success or 400 if error
   */
  @PutMapping("/edit/{id}")
  ResponseEntity<Object> updateBookField(@PathVariable Long id, @RequestBody CreateUpdateFieldDto request);

  /**
   * The API end-point for deleting a book field
   *
   * @param id book field's id
   * @return ResponseEntity that contains a report message and http response code - 200 if success or 400 if error
   */
  @DeleteMapping("/delete/{id}")
  ResponseEntity<Object> deleteBookField(@PathVariable Long id);
}
