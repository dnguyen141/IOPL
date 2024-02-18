package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.CreateUpdateFieldDto;
import inst.iop.LibraryManager.library.services.BookFieldServiceImpl;
import inst.iop.LibraryManager.utilities.responses.ApiResponseEntityFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@CrossOrigin
@RestController
public class BookFieldControllerImpl implements BookFieldController {

  private final BookFieldServiceImpl bookFieldService;
  private final ApiResponseEntityFactory responseEntityFactory;

  /**
   * The API end-point for listing all book fields
   *
   * @return ResponseEntity that contains a list of book fields and http response code - 200 if success
   */
  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> listAllBookFields() {
    Map<String, Object> details = new HashMap<>();
    details.put("book-fields", bookFieldService.listAllBookFields());

    return responseEntityFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully query book field list", details
    );
  }

  /**
   * The API end-point for getting a book field from id
   *
   * @param id book field's id
   * @return ResponseEntity that contains the book field's information and http response code - 200 if success or
   * 400 if error
   */
  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> getBookFieldById(Long id) {
    Map<String, Object> details = new HashMap<>();
    details.put("book-field", bookFieldService.getBookFieldById(id));

    return responseEntityFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully query book field", details
    );
  }

  /**
   * The API end-point for creating a new book field from string
   *
   * @param request which contains new book field's name
   * @return ResponseEntity that contains a report message and http response code - 200 if success or 400 if error
   */
  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> createBookField(CreateUpdateFieldDto request) {
    bookFieldService.createField(request);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.CREATED, "Successfully create new book field"
    );
  }

  /**
   * The API end-point for editing a book field
   *
   * @param id book field's id
   * @param request which contains book field's new name
   * @return ResponseEntity that contains a report message and http response code - 200 if success or 400 if error
   */
  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> updateBookField(Long id, CreateUpdateFieldDto request) {
    bookFieldService.updateBookFieldById(id, request);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.OK, "Successfully update book field with id " + id
    );
  }

  /**
   * The API end-point for deleting a book field
   *
   * @param id book field's id
   * @return ResponseEntity that contains a report message and http response code - 200 if success or 400 if error
   */
  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> deleteBookField(Long id) {
    bookFieldService.deleteField(id);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.OK, "Successfully delete book field with id " + id
    );
  }
}
