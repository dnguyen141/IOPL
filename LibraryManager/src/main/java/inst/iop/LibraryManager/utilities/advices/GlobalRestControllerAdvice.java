package inst.iop.LibraryManager.utilities.advices;

import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import inst.iop.LibraryManager.utilities.responses.ApiResponseEntityFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import static inst.iop.LibraryManager.utilities.ConstraintViolationSetHandler.convertConstrainViolationSetToMap;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalRestControllerAdvice extends ResponseEntityExceptionHandler {

  @Value("${spring.servlet.multipart.max-file-size}")
  private String maxUploadSize;

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

  @Override
  protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
      MaxUploadSizeExceededException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    return responseEntityFactory.createErrorResponse(HttpStatus.PAYLOAD_TOO_LARGE,
        "Maximum upload size is " + maxUploadSize
    );
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    return responseEntityFactory.createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
    Map<String, String> violations = new HashMap<>();

    for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
      String field = extractFieldNameFromViolation(violation);
      String message = violation.getMessage();
      violations.put(field, message);
    }

    return responseEntityFactory.createErrorWithDetailsResponse(HttpStatus.BAD_REQUEST,
        "Constraint(s) violation detected", violations);
  }

  private static String extractFieldNameFromViolation(ConstraintViolation<?> violation) {
    String fieldName = null;
    for (Path.Node node : violation.getPropertyPath()) {
      fieldName = node.getName();
    }
    return fieldName;
  }
}
