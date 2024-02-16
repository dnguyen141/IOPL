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

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> createUser(RegisterDto request) {
    userService.createUser(request);
    return responseFactory.createSuccessResponse(
        HttpStatus.CREATED, "Successfully create new user"
    );
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> updateOtherUserProfile(Long id, ChangeUserDetailsDto request) {
    userService.updateOtherUserById(id, request);
    return responseFactory.createSuccessResponse(
        HttpStatus.OK, "Successfully update specified user details"
    );
  }

  @Override
  public ResponseEntity<Object> updateUserProfile(ChangeDetailsDto request) {
    userService.updateUserByEmail(request);
    return responseFactory.createSuccessResponse(
        HttpStatus.OK, "Successfully update current user details"
    );
  }

  @Override
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<Object> deleteUserById(Long id) {
    userService.deleteUserById(id);
    return responseFactory.createSuccessResponse(
        HttpStatus.OK, "Successfully delete user with id " + id
    );
  }

  @Override
  public ResponseEntity<Object> deleteUser() {
    userService.deleteUser();
    return responseFactory.createSuccessResponse(
        HttpStatus.OK, "Successfully delete current user"
    );
  }
}
