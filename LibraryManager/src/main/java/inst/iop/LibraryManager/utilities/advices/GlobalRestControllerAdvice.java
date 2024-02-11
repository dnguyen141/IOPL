package inst.iop.LibraryManager.utilities.advices;

import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import inst.iop.LibraryManager.utilities.responses.ApiResponseEntityFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.security.SignatureException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

  private final ApiResponseEntityFactory responseEntityFactory;

  @Override
  protected ResponseEntity<Object> handleNoResourceFoundException(
      NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    return responseEntityFactory.createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }


  @ExceptionHandler(BadRequestDetailsException.class)
  public ResponseEntity<Object> handleBadRequestDetailsException(BadRequestDetailsException e) {
    return responseEntityFactory.createErrorWithDetailsResponse(HttpStatus.BAD_REQUEST, e.getMessage(),
        e.getViolations());
  }

  @ExceptionHandler(SignatureException.class)
  public ResponseEntity<Object> handleSignatureException(SignatureException e) {
    return responseEntityFactory.createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException e) {
    return responseEntityFactory.createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Object> handleAuthenticationException(AuthenticationException e) {
    return responseEntityFactory.createErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException e, HttpHeaders headers, HttpStatusCode status, WebRequest request
  ) {
    return responseEntityFactory.createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException e) {
    return responseEntityFactory.createErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
  }

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request
  ) {
    return responseEntityFactory.createErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
    return responseEntityFactory.createErrorResponse(HttpStatus.FORBIDDEN,
        "Provided type for requested argument(s) is in wrong type");
  }
}
