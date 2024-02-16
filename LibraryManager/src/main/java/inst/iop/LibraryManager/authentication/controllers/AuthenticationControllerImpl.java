package inst.iop.LibraryManager.authentication.controllers;

import inst.iop.LibraryManager.authentication.services.AuthenticationService;
import inst.iop.LibraryManager.authentication.dtos.LoginDto;
import inst.iop.LibraryManager.authentication.dtos.RegisterDto;
import inst.iop.LibraryManager.utilities.responses.ApiResponseEntityFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class AuthenticationControllerImpl implements AuthenticationController {

  private final AuthenticationService authenticationService;
  private final ApiResponseEntityFactory responseEntityFactory;

  @Override
  public ResponseEntity<?> register(RegisterDto request) {
    authenticationService.register(request);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.CREATED, "Successfully register new user"
    );
  }

  @Override
  public ResponseEntity<?> login(LoginDto request) {
    return responseEntityFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully login", authenticationService.login(request)
    );
  }

  @Override
  public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
    return responseEntityFactory.createSuccessWithDataResponse(
        HttpStatus.OK,
        "Successfully refresh token",
        authenticationService.refreshToken(request, response)
    );
  }

  @Override
  public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
    authenticationService.logout(request, response);
    return responseEntityFactory.createSuccessResponse(HttpStatus.OK, "Logout successfully");
  }

  @Override
  public ResponseEntity<?> confirmRegistration(
      @RequestParam("u") String email,
      @RequestParam("c") String code
  ) {
    authenticationService.confirmRegistration(email, code);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.OK, "Registration confirmed. Account is enable"
    );
  }
}
