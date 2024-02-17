package inst.iop.LibraryManager.authentication.controllers;

import inst.iop.LibraryManager.authentication.dtos.LoginDto;
import inst.iop.LibraryManager.authentication.dtos.RegisterDto;
import inst.iop.LibraryManager.authentication.services.AuthenticationService;
import inst.iop.LibraryManager.utilities.responses.ApiResponseEntityFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class AuthenticationControllerImpl implements AuthenticationController {

  private final AuthenticationService authenticationService;
  private final ApiResponseEntityFactory responseEntityFactory;

  /**
   * The exposed API end-point for user registration. Also send email via SendGrid which contains confirmation code
   * if the registration succeeded.
   *
   * @param request RegisterDto that provides every information needed for registration
   * @return ResponseEntity that contains a message and http response code - 201 for success and 400 for error.
   *         In error case it will return a list of input or system violations
   */
  @Override
  public ResponseEntity<Object> register(RegisterDto request) {
    authenticationService.register(request);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.CREATED, "Successfully register new user"
    );
  }

  /**
   * The exposed API end-point for user login.
   *
   * @param request LoginDto which contains username and password input
   * @return ResponseEntity that contains a message, http response code - 201 for success and 400 for error,
   *         access and refresh JWT. In error case it will return a list of input or database violations
   */
  @Override
  public ResponseEntity<Object> login(LoginDto request) {
    return responseEntityFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully login", authenticationService.login(request)
    );
  }

  /**
   * The exposed API end-point to refresh user token that generates new access and refresh JWT
   *
   * @param request sent request from user
   * @param response response from server
   * @return ResponseEntity that contains a message, http response code - 201 for success and 400 for error,
   *         new access JWT and refresh JWT. In error case it will return a list of input or database violations
   */
  @Override
  public ResponseEntity<Object> refreshToken(HttpServletRequest request, HttpServletResponse response) {
    return responseEntityFactory.createSuccessWithDataResponse(
        HttpStatus.OK,
        "Successfully refresh token",
        authenticationService.refreshToken(request, response)
    );
  }

  /**
   * The exposed API end-point to user logout.
   *
   * @param request sent request from user
   * @param response response from server
   * @return ResponseEntity that contains a message and http response code.
   */
  @Override
  public ResponseEntity<Object> logout(HttpServletRequest request, HttpServletResponse response) {
    authenticationService.logout(request, response);
    return responseEntityFactory.createSuccessResponse(HttpStatus.OK, "Logout successfully");
  }

  /**
   * The exposed API end-point to confirm registration
   *
   * @param email username
   * @param confirmationCode confirmation code input by user
   * @return ResponseEntity that contains a message, http response code - 200 for success or 400 for error.
   *         In error case, it also returns a map of violations from input or verification process.
   */
  @Override
  public ResponseEntity<Object> confirmRegistration(
      @RequestParam("u") String email,
      @RequestParam("c") String confirmationCode
  ) {
    authenticationService.confirmRegistration(email, confirmationCode);
    return responseEntityFactory.createSuccessResponse(
        HttpStatus.OK, "Registration confirmed. Account is enable"
    );
  }
}
