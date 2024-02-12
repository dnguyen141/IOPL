package inst.iop.LibraryManager.library.controllers;

import inst.iop.LibraryManager.library.dtos.CreateUpdateTypeDto;
import inst.iop.LibraryManager.library.entities.BookType;
import inst.iop.LibraryManager.library.services.BookTypeServiceImpl;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/book-type")
public class BookTypeControllerImpl implements BookTypeController {

  private final BookTypeServiceImpl bookTypeService;
  private final ApiResponseEntityFactory responseEntityFactory;

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> listAllBookTypes() {
    Map<String, Object> details = new HashMap<>();
    details.put("book-types", bookTypeService.getAllBookTypes());

    return responseEntityFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully query book type list", details
    );
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> getBookTypeById(Long id) {
    Map<String, Object> details = new HashMap<>();
    details.put("book-type", bookTypeService.getBookTypeById(id));

    return responseEntityFactory.createErrorWithDetailsResponse(
        HttpStatus.OK, "Successfully query book type details", details
    );
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> createBookType(@Valid CreateUpdateTypeDto request, BindingResult bindingResult) {
    BookType bt = bookTypeService.getBookTypeFromString(request.getName(), true);
    if (bt != null) {
      return responseEntityFactory.createErrorResponse(
          HttpStatus.BAD_REQUEST, "Book type with name \"" + request.getName() + "\" or similar is already " +
              "exists"
      );
    }

    bookTypeService.createType(request.getName(), bindingResult);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.CREATED, "Successfully create new book type"
    );
  }

  @Override
  public ResponseEntity<Object> editBookType(Long id, @Valid CreateUpdateTypeDto request, BindingResult bindingResult) {
    bookTypeService.updateBookTypeById(id, request.getName(), bindingResult);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.ACCEPTED, "Successfully update book type with id " + id
    );
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> deleteBookType(Long id) {
    bookTypeService.deleteTypeById(id);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.NO_CONTENT, "Successfully delete book type"
    );
  }
}
