package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.CreateUpdateFieldDto;
import inst.iop.LibraryManager.library.entities.BookField;
import inst.iop.LibraryManager.library.services.BookFieldServiceImpl;
import inst.iop.LibraryManager.utilities.responses.ApiResponseEntityFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class BookFieldControllerImpl implements BookFieldController {

  private final BookFieldServiceImpl bookFieldService;
  private final ApiResponseEntityFactory responseEntityFactory;

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> listAllBookFields() {
    Map<String, Object> details = new HashMap<>();
    details.put("book_fields", bookFieldService.getAllBookFields());

    return responseEntityFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully query book field list", details
    );
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> getBookFieldById(Long id) {
    Map<String, Object> details = new HashMap<>();
    details.put("book_field", bookFieldService.getBookFieldById(id));

    return responseEntityFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully query book field", details
    );
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> createBookField(@Valid CreateUpdateFieldDto request, BindingResult bindingResult) {
    Optional<BookField> bf = bookFieldService.getBookFieldFromString(request.getName());
    if (bf.isPresent()) {
      return responseEntityFactory.createErrorResponse(
          HttpStatus.BAD_REQUEST, "Book field with name \"" + request.getName() + "\" or similar is already " +
              "exists"
      );
    }

    bookFieldService.createField(request.getName(), bindingResult);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.CREATED, "Successfully create new book field"
    );
  }

  @Override
  public ResponseEntity<Object> updateBookField(Long id, @Valid CreateUpdateFieldDto request,
                                                BindingResult bindingResult) {
    bookFieldService.updateBookFieldById(id, request.getName(), bindingResult);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.ACCEPTED, "Successfully update book field with id " + id
    );
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> deleteBookField(Long id) {
    bookFieldService.deleteField(id);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.NO_CONTENT, "Successfully delete book field with id " + id
    );
  }
}
