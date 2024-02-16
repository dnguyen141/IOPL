package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.CreateUpdateFieldDto;
import inst.iop.LibraryManager.library.services.BookFieldServiceImpl;
import inst.iop.LibraryManager.utilities.responses.ApiResponseEntityFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@CrossOrigin
@RestController
public class BookFieldControllerImpl implements BookFieldController {

  private final BookFieldServiceImpl bookFieldService;
  private final ApiResponseEntityFactory responseEntityFactory;

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> listAllBookFields() {
    Map<String, Object> details = new HashMap<>();
    details.put("book-fields", bookFieldService.listAllBookFields());

    return responseEntityFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully query book field list", details
    );
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> getBookFieldById(Long id) {
    Map<String, Object> details = new HashMap<>();
    details.put("book-field", bookFieldService.getBookFieldById(id));

    return responseEntityFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully query book field", details
    );
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> createBookField(CreateUpdateFieldDto request) {
    bookFieldService.createField(request);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.CREATED, "Successfully create new book field"
    );
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> updateBookField(Long id, CreateUpdateFieldDto request) {
    bookFieldService.updateBookFieldById(id, request);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.OK, "Successfully update book field with id " + id
    );
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> deleteBookField(Long id) {
    bookFieldService.deleteField(id);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.OK, "Successfully delete book field with id " + id
    );
  }
}
