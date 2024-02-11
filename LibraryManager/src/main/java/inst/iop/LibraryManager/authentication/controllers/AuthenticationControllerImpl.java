package inst.iop.LibraryManager.authentication.controllers;

import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import inst.iop.LibraryManager.authentication.dtos.LoginDto;
import inst.iop.LibraryManager.authentication.dtos.RegisterDto;
import inst.iop.LibraryManager.utilities.InformationResponse;
import inst.iop.LibraryManager.utilities.InformationWithDetailsResponse;
import inst.iop.LibraryManager.authentication.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class AuthenticationController implements AuthenticationControllerInf{

  private final UserService userService;

  @Override
  public ResponseEntity<?> register(@RequestBody @Valid RegisterDto request, BindingResult bindingResult) {
    try {
      return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(new InformationWithDetailsResponse(
              "success",
              201,
              "Successfully register",
              userService.register(request, bindingResult)
          ));
    } catch (BadRequestDetailsException e) {
      return ResponseEntity
          .badRequest()
          .body(new InformationWithDetailsResponse(
              "error",
              400,
              "Invalid register request",
              e.getViolations()
          ));
    }
  }

  @Override
  public ResponseEntity<?> login(@RequestBody LoginDto request) {
    try {
      return ResponseEntity
          .accepted()
          .body(new InformationWithDetailsResponse(
              "success",
              HttpStatus.ACCEPTED.value(),
              "Successfully login",
              userService.login(request)
          ));
    } catch (BadRequestDetailsException e) {
      return ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .body(new InformationWithDetailsResponse(
              "error",
              401,
              "Unable to login",
              e.getViolations()
          ));
    }
  }

  @Override
  public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
    try {
      userService.refreshToken(request, response);
      return ResponseEntity
          .status(HttpStatus.ACCEPTED)
          .body(new InformationResponse(
              "success",
              202,
              "Successfully refresh token"
          ));
    } catch (BadRequestDetailsException e) {
      return ResponseEntity
          .badRequest()
          .body(new InformationWithDetailsResponse(
              "error",
              400,
              "Invalid register request",
              e.getViolations()
          ));
    }
  }
}
