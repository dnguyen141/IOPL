package inst.iop.LibraryManager.authentication.controllers;

import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.authentication.entities.enums.Role;
import inst.iop.LibraryManager.authentication.models.requests.ChangeDetailsRequest;
import inst.iop.LibraryManager.authentication.models.requests.ChangeUserDetailsRequest;
import inst.iop.LibraryManager.authentication.models.requests.DeleteUserRequest;
import inst.iop.LibraryManager.authentication.models.requests.RegisterRequest;
import inst.iop.LibraryManager.authentication.models.responses.*;
import inst.iop.LibraryManager.authentication.repositories.UserRepository;
import static inst.iop.LibraryManager.authentication.utility.UserValidators.*;

import lombok.RequiredArgsConstructor;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @GetMapping("/get")
  public ResponseEntity<?> getUserDetails() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    try {
      User user = userRepository.findUserByEmail(authentication.getName()).orElseThrow();
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
    } catch (NoSuchElementException e) {
      return ResponseEntity
          .badRequest()
          .body(
              new InformationResponse(
                  "error",
                  400,
                  "User not found"
              )
          );
    }
  }

  @GetMapping("/all")
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<?> getAllUsers() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String callerRole = authentication.getAuthorities().stream().toList().get(0).toString();
    List<User> query = userRepository.findAllUsers();
    if (callerRole.equals("ROLE_ADMIN")) {
      query.addAll(userRepository.findAllModerators());
      query.addAll(userRepository.findAllAdmins());
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

  @PostMapping("/create")
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<?> createUser(@RequestBody RegisterRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Map<String, Object> violations = validateUserRequest(request, userRepository, true);

    if (!violations.containsKey("role")) {
      String callerRole = authentication.getAuthorities().stream().toList().get(0).toString();
      String targetRole = request.getRole();
      if (callerRole.equals("ROLE_MODERATOR") && compareRole(callerRole, targetRole) <= 0) {
        violations.put("role", "You are not permitted to create an user that has more privileges that you");
      }
    }

    if (violations.isEmpty()) {
      User user = User.builder()
          .email(request.getEmail())
          .password(passwordEncoder.encode(request.getPassword()))
          .firstName(request.getFirstName())
          .lastName(request.getLastName())
          .role(Role.valueOf(request.getRole()))
          .borrowEntries(new HashSet<>())
          .build();
      userRepository.save(user);
      return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(new InformationResponse(
              "success",
              201,
              "Successfully created user"
          ));
    }

    return ResponseEntity
        .badRequest()
        .body(new InformationWithDetailsResponse(
           "error",
           400,
            "Invalid create request",
            violations
        ));
  }

  @PutMapping("/change_user")
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<?> updateUserDetailsAsManager(@RequestBody ChangeUserDetailsRequest request) {
    Map<String, Object> violations = validateUserRequest(request, userRepository, false);
    if (!violations.isEmpty()) {
      return ResponseEntity
          .badRequest()
          .body(new InformationWithDetailsResponse(
              "error",
              400,
              "Invalid change user details using email request",
              violations
          ));
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user;
    try {
      user = userRepository.findUserByEmail(request.getEmail()).orElseThrow();
    } catch (NoSuchElementException e) {
      return ResponseEntity
          .badRequest()
          .body(new InformationResponse(
              "error",
              400,
              "User " + request.getEmail() + " not found"
          )
      );
    }

    String callerRole = authentication.getAuthorities().stream().toList().get(0).toString();
    String targetRole = "ROLE_" + user.getRole().toString();
    if (compareRole(callerRole, targetRole) > 0) {
      userRepository.updateUserByEmail(
          request.getEmail(),
          request.getPassword() == null ? null : passwordEncoder.encode(request.getPassword()),
          request.getFirstName(),
          request.getLastName(),
          request.getRole() == null ? null : Role.valueOf(request.getRole())
      );

      return ResponseEntity
          .ok(new InformationResponse(
              "success",
              201,
              "Successfully update user details"
          ));
    }
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(new InformationResponse(
            "error",
            401,
            "You are not allowed to perform this action"
        ));
  }

  @PutMapping("/change")
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_USER'})")
  public ResponseEntity<?> updateDetailsAsUser(@RequestBody ChangeDetailsRequest request) {
    Map<String, Object> violations = validateUserRequest(request, userRepository, false);
    if (!violations.isEmpty()) {
      return ResponseEntity
          .badRequest()
          .body(new InformationWithDetailsResponse(
              "error",
              400,
              "Invalid change user details request",
              new HashMap<>(violations)
          ));
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    userRepository.updateUserByEmail(
        authentication.getName(),
        request.getPassword() != null ? passwordEncoder.encode(request.getPassword()) : null,
        request.getFirstName(),
        request.getLastName(),
        null
    );
    return ResponseEntity
        .ok(new InformationResponse(
            "success",
            201,
            "Successfully update user details"
        ));
  }

  @DeleteMapping("/delete_user")
  @PreAuthorize("hasAnyRole({'ROLE_ADMIN', 'ROLE_MODERATOR'})")
  public ResponseEntity<?> deleteUserByEmail(@RequestBody DeleteUserRequest request) {
    User user;
    try {
      user = userRepository.findUserByEmail(request.getEmail()).orElseThrow();
    } catch (NoSuchElementException e) {
      return ResponseEntity
          .badRequest()
          .body(new InformationResponse(
                  "error",
                  400,
                  "User " + request.getEmail() + " not found"
              )
          );
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String callerRole = authentication.getAuthorities().stream().toList().get(0).toString();
    String targetRole = "ROLE_" + user.getRole().toString();

    if (compareRole(callerRole, targetRole) > 0) {
      userRepository.deleteUserByEmail(request.getEmail());
      return ResponseEntity.ok(
          new InformationResponse(
              "success",
              202,
              "Delete user successfully"
          )
      );
    }

    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(new InformationResponse(
            "error",
            401,
            "You are not allowed to perform this action"
        ));
  }

  @DeleteMapping("/delete")
  @PreAuthorize("hasAnyRole({'ROLE_USER'})")
  public ResponseEntity<?> deleteUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    userRepository.deleteUserByEmail(authentication.getName());
    SecurityContextHolder.getContext().setAuthentication(null);
    return ResponseEntity.ok(
        new InformationResponse(
            "success",
            202,
            "Delete user successfully"
        )
    );
  }
}
