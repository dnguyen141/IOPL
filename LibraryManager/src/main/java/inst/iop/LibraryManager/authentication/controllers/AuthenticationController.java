package inst.iop.LibraryManager.authentication.controllers;

import inst.iop.LibraryManager.authentication.exceptions.BadRequestDetailsException;
import inst.iop.LibraryManager.authentication.models.requests.LoginRequest;
import inst.iop.LibraryManager.authentication.models.requests.RegisterRequest;
import inst.iop.LibraryManager.authentication.models.responses.InformationResponse;
import inst.iop.LibraryManager.authentication.models.responses.InformationWithDetailsResponse;
import inst.iop.LibraryManager.authentication.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    try {
      return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    } catch (BadRequestDetailsException e) {
      return ResponseEntity
          .badRequest()
          .body(new InformationWithDetailsResponse(
              "error",
              400,
              "Invalid register request",
              new HashMap<>(e.getViolations())
          ));
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    try {
      return ResponseEntity.accepted().body(userService.login(request));
    } catch (Exception e) {
      return ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .body(new InformationWithDetailsResponse(
              "error",
              401,
              "Email or password is invalid",
              new HashMap<>()
          ));
    }
  }

  @PostMapping("/refresh-token")
  public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
    userService.refreshToken(request, response);
  }


  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
    userService.logout(request, response);
    return ResponseEntity.ok(
        new InformationResponse(
            "success",
            200,
            "Logout successfully"
        )
    );
  }
}
