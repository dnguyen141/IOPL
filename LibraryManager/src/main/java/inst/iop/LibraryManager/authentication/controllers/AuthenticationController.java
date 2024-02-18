package inst.iop.LibraryManager.authentication.controllers;

import inst.iop.LibraryManager.authentication.dtos.LoginDto;
import inst.iop.LibraryManager.authentication.dtos.RegisterDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/auth")
@Validated
public interface AuthenticationController {

  /**
   * The exposed API end-point for user registration. Also send email via SendGrid which contains confirmation code
   * if the registration succeeded.
   *
   * @param request RegisterDto that provides every information needed for registration
   * @return ResponseEntity that contains a message and http response code - 201 for success or 400 for error.
   *         In error case it will return a map of input or system violations
   */
  @PostMapping("/register")
  ResponseEntity<Object> register(@RequestBody RegisterDto request);

  /**
   * The exposed API end-point for user login.
   *
   * @param request LoginDto which contains username and password input
   * @return ResponseEntity that contains a message, http response code - 201 for success or 400 for error,
   *         access and refresh JWT in success case or a map of input or database violations in error case
   */
  @PostMapping("/login")
  ResponseEntity<Object> login(@RequestBody LoginDto request);

  /**
   * The exposed API end-point to refresh user token that generates new access and refresh JWT
   *
   * @param request sent request from user
   * @param response response from server
   * @return ResponseEntity that contains a message, http response code - 201 for success and 400 for error,
   *         new access JWT and refresh JWT in success case and a map violations in error case
   */
  @PostMapping("/refresh-token")
  ResponseEntity<Object> refreshToken(HttpServletRequest request, HttpServletResponse response);

  /**
   * The exposed API end-point for user logout.
   *
   * @param request sent request from user
   * @param response response from server
   * @return ResponseEntity that contains a message and http response code 200 in success case
   */
  @PostMapping("/logout")
  ResponseEntity<Object> logout(HttpServletRequest request, HttpServletResponse response);

  /**
   * The exposed API end-point to confirm registration
   *
   * @param email username
   * @param confirmationCode confirmation code input by user
   * @return ResponseEntity that contains a message, http response code - 200 for success or 400 for error.
   *         In error case, it also returns a map of violations from input or verification process.
   */
  @GetMapping("/confirm")
  ResponseEntity<Object> confirmRegistration(@RequestParam("u") String email,
                                             @RequestParam("c") String confirmationCode);
}
