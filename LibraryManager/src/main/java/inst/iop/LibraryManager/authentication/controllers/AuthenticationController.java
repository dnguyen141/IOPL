package inst.iop.LibraryManager.authentication.controllers;

import inst.iop.LibraryManager.authentication.dtos.LoginDto;
import inst.iop.LibraryManager.authentication.dtos.RegisterDto;
import inst.iop.LibraryManager.authentication.entities.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/auth")
@Tag(name = "authentication-controller")
@Validated
public interface AuthenticationController {

  @Operation(summary = "Register a new user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully register new user",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to register new user due to invalid inputs",
          content = @Content)
  })
  @PostMapping("/register")
  ResponseEntity<Object> register(@Parameter(description = "Vital information for registration")
                                  @RequestBody RegisterDto request);

  @Operation(summary = "Log an user in")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully login",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to register new user due to invalid inputs",
          content = @Content)
  })
  @PostMapping("/login")
  ResponseEntity<Object> login(@Parameter(description = "Login information from user")
                               @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody LoginDto request);

  @Operation(summary = "Refresh JWT of an user in case it expires. Only for logged in users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully refresh token",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to refresh token",
          content = @Content)
  })
  @PostMapping("/refresh-token")
  ResponseEntity<Object> refreshToken(HttpServletRequest request, HttpServletResponse response);

  @Operation(summary = "Log an user out")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully logout",
          content = @Content)
  })
  @PostMapping("/logout")
  ResponseEntity<Object> logout(HttpServletRequest request, HttpServletResponse response);

  @Operation(summary = "Confirm new user registration. Only for logged in users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully confirm new user",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Unable to confirm registration",
          content = @Content)
  })
  @GetMapping("/confirm")
  ResponseEntity<Object> confirmRegistration(@Parameter(description = "email of the user that needs to be confirmed")
                                             @RequestParam("u") String email,
                                             @Parameter(description = "confirmation code that was sent per email")
                                             @RequestParam("c") String confirmationCode);
}
