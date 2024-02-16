package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.CreateUpdateTypeDto;
import inst.iop.LibraryManager.library.services.BookTypeService;
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
public class BookTypeControllerImpl implements BookTypeController {

  private final BookTypeService bookTypeService;
  private final ApiResponseEntityFactory responseEntityFactory;

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> listAllBookTypes() {
    Map<String, Object> details = new HashMap<>();
    details.put("book-types", bookTypeService.listAllBookTypes());

    return responseEntityFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully query book type list", details
    );
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> getBookTypeById(Long id) {
    Map<String, Object> details = new HashMap<>();
    details.put("book-type", bookTypeService.getBookTypeById(id));

    return responseEntityFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully query book type details", details
    );
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> createBookType(CreateUpdateTypeDto request) {
    bookTypeService.createType(request);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.CREATED, "Successfully create new book type"
    );
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> updateBookType(Long id, CreateUpdateTypeDto request) {
    bookTypeService.updateBookTypeById(id, request);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.OK, "Successfully update book type with id " + id
    );
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> deleteBookType(Long id) {
    bookTypeService.deleteTypeById(id);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.OK, "Successfully delete book type"
    );
  }
}
