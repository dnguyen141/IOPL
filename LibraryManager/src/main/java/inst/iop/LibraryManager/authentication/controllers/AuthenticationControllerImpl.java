package inst.iop.LibraryManager.authentication.controllers;

import inst.iop.LibraryManager.authentication.services.AuthenticationService;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import inst.iop.LibraryManager.authentication.dtos.LoginDto;
import inst.iop.LibraryManager.authentication.dtos.RegisterDto;
import inst.iop.LibraryManager.utilities.responses.ApiResponseEntityFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static inst.iop.LibraryManager.utilities.BindingResultHandler.handleBindingResult;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/auth")
public class AuthenticationControllerImpl implements AuthenticationController {

  private final AuthenticationService authenticationService;
  private final ApiResponseEntityFactory responseEntityFactory;

  @Override
  public ResponseEntity<Object> register(@RequestBody @Valid RegisterDto request, BindingResult bindingResult) {
    try {
      Map<String, Object> violations = handleBindingResult(bindingResult);
      if (!violations.isEmpty()) {
        throw new BadRequestDetailsException(violations);
      }

      authenticationService.register(request, bindingResult);
      return responseEntityFactory.createSuccessResponse(
          HttpStatus.CREATED, "Successfully register new user"
      );
    } catch (BadRequestDetailsException e) {
      return responseEntityFactory.createErrorWithDetailsResponse(
          HttpStatus.BAD_REQUEST, "Invalid register request", e.getViolations()
      );
    }
  }

  @Override
  public ResponseEntity<Object> login(@RequestBody LoginDto request) {
    try {
      return responseEntityFactory.createSuccessWithDataResponse(
          HttpStatus.ACCEPTED, "Successfully login", authenticationService.login(request)
      );
    } catch (BadRequestDetailsException e) {
      return responseEntityFactory.createErrorWithDetailsResponse(
          HttpStatus.UNAUTHORIZED, "Unable to login", e.getViolations()
      );
    }
  }

  @Override
  public ResponseEntity<Object> refreshToken(HttpServletRequest request, HttpServletResponse response) {
    try {
      return responseEntityFactory.createSuccessWithDataResponse(
          HttpStatus.ACCEPTED,
          "Successfully refresh token",
          authenticationService.refreshToken(request, response)
      );
    } catch (BadRequestDetailsException e) {
      return responseEntityFactory.createErrorWithDetailsResponse(
          HttpStatus.BAD_REQUEST, "Unable to refresh token", e.getViolations()
      );
    }
  }

  @Override
  public ResponseEntity<Object> logout(HttpServletRequest request, HttpServletResponse response) {
    authenticationService.logout(request, response);
    return responseEntityFactory.createSuccessResponse(HttpStatus.ACCEPTED, "Logout successfully");
  }

  @Override
  public ResponseEntity<Object> confirmRegistration(
      @RequestParam("u") String email,
      @RequestParam("c") String code
  ) {
    authenticationService.confirmRegister(email, code);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.ACCEPTED, "Registration confirmed. Account is enable"
    );
  }
}
