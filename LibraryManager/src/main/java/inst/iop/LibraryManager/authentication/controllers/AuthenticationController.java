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
   * The exposed API end-point for user registration
   *
   * @param request RegisterDto that provides every information needed for registration
   * @return The ResponseEntity of type Object that output the result or error in json format
   */
  @PostMapping("/register")
  ResponseEntity<Object> register(@RequestBody RegisterDto request);

  @PostMapping("/login")
  ResponseEntity<Object> login(@RequestBody LoginDto request);

  @PostMapping("/refresh-token")
  ResponseEntity<Object> refreshToken(HttpServletRequest request, HttpServletResponse response);

  @PostMapping("/logout")
  ResponseEntity<Object> logout(HttpServletRequest request, HttpServletResponse response);

  @GetMapping("/confirm")
  ResponseEntity<Object> confirmRegistration(@RequestParam("u") String email,
                                        @RequestParam("c") String confirmationCode);
}
