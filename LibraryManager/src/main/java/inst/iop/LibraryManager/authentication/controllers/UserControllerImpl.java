package inst.iop.LibraryManager.authentication.controllers;

import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.authentication.dtos.ChangeDetailsDto;
import inst.iop.LibraryManager.authentication.dtos.ChangeUserDetailsDto;
import inst.iop.LibraryManager.authentication.dtos.RegisterDto;

import inst.iop.LibraryManager.authentication.services.UserService;
import inst.iop.LibraryManager.utilities.InformationResponse;
import inst.iop.LibraryManager.utilities.InformationWithDetailsResponse;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerInterface {
  private final UserService userService;

  @Override
  public ResponseEntity<?> getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user;
    try {
      user = userService.findUserByEmail(authentication.getName());
    } catch (BadRequestDetailsException e) {
      return ResponseEntity
          .badRequest()
          .body(
              new InformationWithDetailsResponse(
                  "error",
                  400,
                  "Unable to get user details",
                  e.getViolations()
              )
          );
    }

    Map<String, Object> details = new HashMap<>();
    details.put("user", user);

    return ResponseEntity.ok(
        new InformationWithDetailsResponse(
            "success",
            200,
            "Successfully queried user details",
            details
        )
    );
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<?> getUserDetailsById(@PathVariable Long id) {
    User user;
    try {
      user = userService.findUserById(id);
    } catch (BadRequestDetailsException e) {
      return ResponseEntity
          .badRequest()
          .body(
              new InformationWithDetailsResponse(
                  "error",
                  400,
                  "Unable to get user details",
                  e.getViolations()
              )
          );
    }

    Map<String, Object> details = new HashMap<>();
    details.put("user", user);

    return ResponseEntity.ok(
        new InformationWithDetailsResponse(
            "success",
            200,
            "Successfully get user details",
            details
        )
    );
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<?> getAllUsers() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String callerRole = authentication.getAuthorities().stream().toList().get(0).toString();
    List<User> query = userService.findAllUsers();

    if (callerRole.equals("ROLE_ADMIN")) {
      query.addAll(userService.findAllModerators());
      query.addAll(userService.findAllAdmins());
    }

    Map<String, Object> details = new HashMap<>();
    details.put("users", query);
    return ResponseEntity.ok(
        new InformationWithDetailsResponse(
            "success",
            200,
            "Successfully queried users list",
            details
        )
    );
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<?> createUser(@RequestBody @Valid RegisterDto request, BindingResult bindingResult) {
    try {
      userService.createUser(request, bindingResult);
      return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(new InformationResponse(
              "success",
              201,
              "Successfully created user"
          ));
    } catch (BadRequestDetailsException e) {
      return ResponseEntity
          .badRequest()
          .body(new InformationWithDetailsResponse(
              "error",
              400,
              "Invalid create request",
              e.getViolations()
          ));
    }
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  @Transactional
  public ResponseEntity<?> updateOtherUserProfile(@RequestBody @Valid ChangeUserDetailsDto request,
                                                      BindingResult bindingResult) {
    try {
      userService.updateUserByEmail(request, bindingResult);
    } catch (BadRequestDetailsException e) {
      return ResponseEntity
          .badRequest()
          .body(new InformationWithDetailsResponse(
              "error",
              400,
              "Unable to update user details",
              e.getViolations()
          ));
    }

    return ResponseEntity
        .ok(new InformationResponse(
            "success",
            201,
            "Successfully update user details"
        ));
  }

  @Override
  @PreAuthorize("hasRole('ROLE_USER')")
  @Transactional
  public ResponseEntity<?> updateUserProfile(@RequestBody @Valid ChangeDetailsDto request,
                                               BindingResult bindingResult) {
    try {
      userService.updateUserByEmail(request, bindingResult);
    } catch (BadRequestDetailsException e) {
      return ResponseEntity
          .badRequest()
          .body(new InformationWithDetailsResponse(
              "error",
              400,
              "Unable to update profile",
              e.getViolations()
          ));
    }

    return ResponseEntity
        .ok(new InformationResponse(
            "success",
            201,
            "Successfully update profile"
        ));
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  @Transactional
  public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
    try {
      userService.deleteUserById(id);
      return ResponseEntity
          .status(HttpStatus.OK)
          .body(new InformationResponse(
              "success",
              201,
              "Successfully delete user with id " + id
          ));
    } catch (BadRequestDetailsException e) {
      return ResponseEntity
          .badRequest()
          .body(new InformationWithDetailsResponse(
             "error",
             400,
             "Unable to delete user with id " + id,
             e.getViolations()
          ));
    }
  }

  @Override
  @Transactional
  public ResponseEntity<?> deleteUser() {
    userService.deleteUser();
    return ResponseEntity.ok(
        new InformationResponse(
            "success",
            202,
            "Delete user successfully"
        )
    );
  }

  @Override
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
