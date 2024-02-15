package inst.iop.LibraryManager.utilities.responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ApiResponseEntityFactory {

  ResponseEntity<Object> createSuccessResponse(HttpStatus httpStatus, String message);

  ResponseEntity<Object> createSuccessWithDataResponse(HttpStatus httpStatus, String message,
                                                       Map<String, Object> data);

  ResponseEntity<Object> createErrorResponse(HttpStatus httpStatus, String message);

  ResponseEntity<Object> createErrorWithDetailsResponse(HttpStatus httpStatus, String message,
                                                        Map<String, String> violations);
}
