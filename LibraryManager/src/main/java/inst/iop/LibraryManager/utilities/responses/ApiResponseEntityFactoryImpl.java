package inst.iop.LibraryManager.utilities.responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ApiResponseEntityFactoryImpl implements ApiResponseEntityFactory {

  @Override
  public ResponseEntity<Object> createSuccessResponse(HttpStatus httpStatus, String message) {
    return ResponseEntity
        .status(httpStatus)
        .contentType(MediaType.APPLICATION_JSON)
        .body(new InformationApiResponse("success", httpStatus.value(), message));
  }

  @Override
  public ResponseEntity<Object> createSuccessWithDataResponse(
      HttpStatus httpStatus, String message, Map<String, Object> data) {
    return ResponseEntity
        .status(httpStatus)
        .contentType(MediaType.APPLICATION_JSON)
        .body(new SuccessWithDataApiResponse(httpStatus.value(), message, data));
  }

  @Override
  public ResponseEntity<Object> createErrorResponse(HttpStatus httpStatus, String message) {
    return ResponseEntity
        .status(httpStatus)
        .contentType(MediaType.APPLICATION_JSON)
        .body(new InformationApiResponse("error", httpStatus.value(), message));
  }

  @Override
  public ResponseEntity<Object> createErrorWithDetailsResponse(
      HttpStatus httpStatus, String message, Map<String, Object> violations) {
    return ResponseEntity
        .status(httpStatus)
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ErrorWithViolationsApiResponse(httpStatus.value(), message, violations));
  }
}
