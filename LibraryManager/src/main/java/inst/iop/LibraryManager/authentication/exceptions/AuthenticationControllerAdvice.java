package inst.iop.LibraryManager.authentication.exceptions;

import inst.iop.LibraryManager.authentication.models.responses.InformationResponse;
import inst.iop.LibraryManager.authentication.models.responses.InformationWithDetailsResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class AuthenticationControllerAdvice extends ResponseEntityExceptionHandler {

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<InformationResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
    return ResponseEntity
        .badRequest()
        .body(new InformationResponse(
            "error",
            400,
            e.getMessage()
        ));
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<InformationResponse> handleAuthenticationException(AuthenticationException e) {
    return ResponseEntity
        .badRequest()
        .body(new InformationResponse(
            "error",
            400,
            e.getMessage()
        ));
  }

  @ExceptionHandler(BadRequestDetailsException.class)
  public ResponseEntity<InformationWithDetailsResponse> handleBadRequestDetailsException(BadRequestDetailsException e) {
    return ResponseEntity
        .badRequest()
        .body(new InformationWithDetailsResponse(
            "error",
            400,
            e.getMessage(),
            e.getViolations()
        ));
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    Map<String, Object> details = new HashMap<>();
    details.put("cause", ex.getCause().toString());

    return ResponseEntity
        .badRequest()
        .body(new InformationWithDetailsResponse(
            "error",
            status.value(),
            ex.getMessage(),
            details
        ));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<InformationWithDetailsResponse> handleAccessDeniedException(AccessDeniedException e) {
    Map<String, Object> details = new HashMap<>();
    details.put("causes", e.getCause().toString());

    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(new InformationWithDetailsResponse(
            "error",
            403,
            e.getMessage(),
            details
        ));
  }

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    return ResponseEntity
        .badRequest()
        .body(new InformationResponse(
            "error",
            status.value(),
            ex.getMessage()
        ));
  }
}
