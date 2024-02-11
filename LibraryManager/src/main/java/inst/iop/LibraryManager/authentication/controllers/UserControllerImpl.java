package inst.iop.LibraryManager.authentication.controllers;

import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.authentication.dtos.ChangeDetailsDto;
import inst.iop.LibraryManager.authentication.dtos.ChangeUserDetailsDto;
import inst.iop.LibraryManager.authentication.dtos.RegisterDto;

import inst.iop.LibraryManager.authentication.services.UserService;
import inst.iop.LibraryManager.utilities.responses.ApiResponseEntityFactory;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
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
public class UserControllerImpl implements UserController {
  private final UserService userService;
  private final ApiResponseEntityFactory responseFactory;

  @Override
  public ResponseEntity<Object> getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user;
    try {
      user = userService.findUserByEmail(authentication.getName());
    } catch (BadRequestDetailsException e) {
      return responseFactory.createErrorResponse(
          HttpStatus.BAD_REQUEST,
          "Unable to get user details"
      );
    }

    Map<String, Object> data = new HashMap<>();
    data.put("user", user);

    return responseFactory.createSuccessWithDataResponse(HttpStatus.OK, "Successfully query user details",
        data);
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> getUserById(@PathVariable Long id) {
    User user;
    try {
      user = userService.findUserById(id);
    } catch (BadRequestDetailsException e) {
      return responseFactory.createErrorResponse(
          HttpStatus.BAD_REQUEST,
          "Unable to get user details"
      );
    }

    Map<String, Object> data = new HashMap<>();
    data.put("user", user);

    return responseFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully query user details", data
    );
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> listAllUsers() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String callerRole = authentication.getAuthorities().stream().toList().get(0).toString();
    List<User> query = userService.findAllUsers();

    if (callerRole.equals("ROLE_ADMIN")) {
      query.addAll(userService.findAllModerators());
      query.addAll(userService.findAllAdmins());
    }

    Map<String, Object> details = new HashMap<>();
    details.put("users", query);
    return responseFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully query users list", details
    );
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> createUser(@RequestBody @Valid RegisterDto request, BindingResult bindingResult) {
    try {
      userService.createUser(request, bindingResult);
      return responseFactory.createSuccessResponse(
          HttpStatus.CREATED, "Successfully create new user"
      );
    } catch (BadRequestDetailsException e) {
      return responseFactory.createErrorWithDetailsResponse(
          HttpStatus.BAD_REQUEST, "Invalid create request", e.getViolations()
      );
    }
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> updateOtherUserProfile(@RequestBody @Valid ChangeUserDetailsDto request,
                                                      BindingResult bindingResult) {
    try {
      userService.updateOtherUserByEmail(request, bindingResult);
      return responseFactory.createSuccessResponse(
          HttpStatus.ACCEPTED, "Successfully update specified user details"
      );
    } catch (BadRequestDetailsException e) {
      return responseFactory.createErrorWithDetailsResponse(
          HttpStatus.BAD_REQUEST, "Unable to update specified user details", e.getViolations()
      );
    }
  }

  @Override
  public ResponseEntity<Object> updateUserProfile(@RequestBody @Valid ChangeDetailsDto request,
                                               BindingResult bindingResult) {
    try {
      userService.updateOtherUserByEmail(request, bindingResult);
      return responseFactory.createSuccessResponse(
          HttpStatus.ACCEPTED, "Successfully update current user details"
      );
    } catch (BadRequestDetailsException e) {
      return responseFactory.createErrorWithDetailsResponse(
          HttpStatus.BAD_REQUEST, "Unable to update current user details", e.getViolations()
      );
    }
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> deleteUserById(@PathVariable Long id) {
    try {
      userService.deleteUserById(id);
      return responseFactory.createSuccessResponse(
          HttpStatus.ACCEPTED, "Successfully delete user with id " + id
      );
    } catch (BadRequestDetailsException e) {
      return responseFactory.createErrorWithDetailsResponse(
          HttpStatus.BAD_REQUEST, "Unable to delete user with id " + id, e.getViolations()
      );
    }
  }

  @Override
  public ResponseEntity<Object> deleteUser() {
    userService.deleteUser();
    return responseFactory.createSuccessResponse(
        HttpStatus.ACCEPTED, "Successfully delete current user"
    );
  }
}
