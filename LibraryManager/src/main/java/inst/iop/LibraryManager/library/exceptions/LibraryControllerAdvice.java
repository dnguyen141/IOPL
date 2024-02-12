package inst.iop.LibraryManager.library.exceptions;

import inst.iop.LibraryManager.utilities.responses.SuccessWithDataApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class LibraryControllerAdvice extends ResponseEntityExceptionHandler {

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<SuccessWithDataApiResponse> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e
  ) {
    Map<String, Object> details = new HashMap<>();
    details.put("causes", e.getCause().toString());

    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(new SuccessWithDataApiResponse(
            "error",
            403,
            e.getMessage(),
            details
        ));
  }
}
