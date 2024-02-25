package inst.iop.LibraryManager.authentication.controllers;

import inst.iop.LibraryManager.authentication.dtos.ChangeDetailsDto;
import inst.iop.LibraryManager.authentication.dtos.ChangeUserDetailsDto;
import inst.iop.LibraryManager.authentication.dtos.RegisterDto;
import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.authentication.services.UserService;
import inst.iop.LibraryManager.utilities.responses.ApiResponseEntityFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {
  private final UserService userService;
  private final ApiResponseEntityFactory responseFactory;

  /**
   * The API end-point to get current user information.
   *
   * @return ResponseEntity that contains a message, http response code, a Map contains user information in success case
   * or violations from input or verification in error case
   */
  @Override
  public ResponseEntity<Object> getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = userService.findUserByEmail(authentication.getName());
    Map<String, Object> data = new HashMap<>();
    data.put("user", user);

    return responseFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully query user details", data
    );
  }

  /**
   * The API end-point for listing a user's information.
   * There are 3 types of user: ADMIN, MODERATOR and USER. ADMINs can see MODERATORs and USERs information,
   * MODERATORs can only see USERs information.
   *
   * @param  id user's id
   * @return ResponseEntity that contains a message, http response code, a list of users in success case or violations
   *         from input or verification in error case
   */
  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> getUserById(Long id) {
    User user = userService.findUserById(id);

    Map<String, Object> data = new HashMap<>();
    data.put("user", user);

    return responseFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully query user details", data
    );
  }

  /**
   * The API end-point for listing users for admins and moderators.
   * There are 3 types of user: ADMIN, MODERATOR and USER. ADMINs can see MODERATORs and USERs information,
   * MODERATORs can only see USERs information.
   *
   * @param  pageNumber page number
   * @param  pageSize number of entries in a page
   * @return ResponseEntity that contains a message, http response code, a list of users in success case or violations
   *         from input or verification in error case
   */
  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> listAllUsers(Integer pageNumber, Integer pageSize) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String callerRole = authentication.getAuthorities().stream().toList().get(0).toString();

    Page<User> userList;
    if (callerRole.equals("ROLE_ADMIN")) {
      userList = userService.findAllModeratorsAndUsers(pageNumber, pageSize);
    } else {
      userList = userService.findAllUsers(pageNumber, pageSize);
    }

    Map<String, Object> details = new HashMap<>();
    details.put("users", userList.getContent().stream());
    details.put("pageNumber", pageNumber);
    details.put("pageSize", pageSize);
    details.put("numberOfPages", userList.getTotalPages());

    return responseFactory.createSuccessWithDataResponse(
        HttpStatus.OK, "Successfully query users list", details
    );
  }

  /**
   * The API end-point for create new user. It also sends email for user to confirm their identity.
   *
   * @param  request create user request
   * @return ResponseEntity that contains a message, http response code, a list of users in success case or violations
   *         from input or verification in error case
   */
  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> createUser(RegisterDto request) {
    userService.createUser(request);
    return responseFactory.createSuccessResponse(
        HttpStatus.CREATED, "Successfully create new user"
    );
  }

  /**
   * The API end-point used by admins and moderators to update other user's profile.
   *
   * @param  request create user request
   * @return ResponseEntity that contains a message, http response code, a list of users in success case or violations
   *         from input or verification in error case
   */
  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> updateOtherUserProfile(Long id, ChangeUserDetailsDto request) {
    userService.updateOtherUserById(id, request);
    return responseFactory.createSuccessResponse(
        HttpStatus.ACCEPTED, "Successfully update specified user details"
    );
  }

  /**
   * The API end-point used by users to update their profile.
   *
   * @param  request create user request
   * @return ResponseEntity that contains a message, http response code, a list of users in success case or violations
   *         from input or verification in error case
   */
  @Override
  public ResponseEntity<Object> updateUserProfile(ChangeDetailsDto request) {
    userService.updateUserByEmail(request);
    return responseFactory.createSuccessResponse(
        HttpStatus.ACCEPTED, "Successfully update current user details"
    );
  }

  /**
   * The API end-point used by admins and moderators to delete a user by their ID. Can only delete if the account has
   * less authority.
   *
   * @param  id User ID to delete.
   * @return ResponseEntity with success message.
   */
  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> deleteUserById(Long id) {
    userService.deleteUserById(id);
    return responseFactory.createSuccessResponse(
        HttpStatus.NO_CONTENT, "Successfully delete user with id " + id
    );
  }

  /**
   * Delete the currently authenticated user.
   *
   * @return ResponseEntity with success message.
   */
  @Override
  public ResponseEntity<Object> deleteUser() {
    userService.deleteUser();
    return responseFactory.createSuccessResponse(
        HttpStatus.NO_CONTENT, "Successfully delete current user"
    );
  }
}
