package inst.iop.LibraryManager.authentication.services;

import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.authentication.entities.enums.Role;
import inst.iop.LibraryManager.authentication.dtos.ChangeDetailsDto;
import inst.iop.LibraryManager.authentication.dtos.ChangeUserDetailsDto;
import inst.iop.LibraryManager.authentication.repositories.TokenRepository;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import inst.iop.LibraryManager.authentication.dtos.RegisterDto;
import inst.iop.LibraryManager.authentication.repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import static inst.iop.LibraryManager.utilities.BindingResultHandler.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public User findUserById(Long id) throws BadRequestDetailsException {
    return userRepository.findUserById(id).orElseThrow(() -> {
        Map<String, Object> violations = new HashMap<>();
        violations.put("id", "There is no user with id " + id);
        return new BadRequestDetailsException(violations);
    });
  }

  @Override
  public User findUserByEmail(String email) throws BadRequestDetailsException {
    return userRepository.findUserByEmail(email).orElseThrow(() -> {
      Map<String, Object> violations = new HashMap<>();
      violations.put("email", "There is no user with email address " + email);
      return new BadRequestDetailsException(violations);
    });
  }

  @Override
  public List<User> findAllUsers() {
    return userRepository.findAllUsers();
  }

  @Override
  public List<User> findAllModerators() {
    return userRepository.findAllModerators();
  }

  @Override
  public List<User> findAllAdmins() {
    return userRepository.findAllAdmins();
  }

  @Override
  @Transactional
  public void createUser(@Valid RegisterDto request, BindingResult bindingResult)
      throws BadRequestDetailsException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Map<String, Object> violations = handleBindingResult(bindingResult);

    if (!violations.containsKey("role")) {
      String callerRole = authentication.getAuthorities().stream().toList().get(0).toString();
      String targetRole = "ROLE_USER";
      if (callerRole.equals("ROLE_MODERATOR") && compareRole(callerRole, targetRole) <= 0) {
        violations.put("role", "You are not permitted to create an user that has more privileges that you");
      }
    }

    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException(violations);
    }

    User user = User.builder()
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .role(Role.USER)
        .enabled(true)
        .borrowEntries(new HashSet<>())
        .build();
    userRepository.save(user);
  }

  @Override
  @Transactional
  public void updateOtherUserByEmail(@Valid ChangeUserDetailsDto userDetailsRequest, BindingResult bindingResult)
      throws BadRequestDetailsException {
    Map<String, Object> violations = handleBindingResult(bindingResult);
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException(violations);
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String callerRole = authentication.getAuthorities().stream().toList().get(0).toString();

    User user = userRepository.findUserByEmail(userDetailsRequest.getEmail()).orElseThrow(() -> {
      violations.put("email", "No user with email " + userDetailsRequest.getEmail() + " found");
      return new BadRequestDetailsException(violations);
    });
    String targetRole = "ROLE_" + user.getRole().toString();

    if (compareRole(callerRole, targetRole) <= 0) {
      violations.put("role", "You are not allowed to perform this action");
      throw new BadRequestDetailsException(violations);
    }

    userRepository.updateUserByEmail(
        userDetailsRequest.getEmail(),
        userDetailsRequest.getPassword() != null && !userDetailsRequest.getPassword().equals(user.getPassword()) ?
            passwordEncoder.encode(userDetailsRequest.getPassword()) : user.getPassword(),
        userDetailsRequest.getFirstName(),
        userDetailsRequest.getLastName(),
        Role.getRoleFromString(userDetailsRequest.getRole())
    );
  }

  @Override
  @Transactional
  public void updateOtherUserByEmail(@Valid ChangeDetailsDto userDetailsRequest, BindingResult bindingResult)
      throws BadRequestDetailsException {
    Map<String, Object> violations = handleBindingResult(bindingResult);
    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException(violations);
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = userRepository.findUserByEmail(authentication.getName()).orElseThrow(() -> {
      violations.put("email", "No user with email " + authentication.getName() + " found");
      return new BadRequestDetailsException(violations);
    });

    userRepository.updateUserByEmail(
        authentication.getName(),
        userDetailsRequest.getPassword() != null && !userDetailsRequest.getPassword().equals(user.getPassword()) ?
            passwordEncoder.encode(userDetailsRequest.getPassword()) : user.getPassword(),
        userDetailsRequest.getFirstName(),
        userDetailsRequest.getLastName(),
        user.getRole()
    );
  }

  private int compareRole(String callerRole, String targetRole) throws IllegalArgumentException {
    return Integer.compare(
        Objects.requireNonNull(Role.getRoleFromAuthority(callerRole)).getOrder(),
        Objects.requireNonNull(Role.getRoleFromAuthority(targetRole)).getOrder()
    );
  }

  @Override
  @Transactional
  public void deleteUserById(Long id) throws BadRequestDetailsException {
    User user = userRepository.findUserById(id).orElseThrow(() -> {
      Map<String, Object> violations = new HashMap<>();
      violations.put("id", "No user with id " + id + " found");
      return new BadRequestDetailsException(violations);
    });

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String callerRole = authentication.getAuthorities().stream().toList().get(0).toString();
    String targetRole = "ROLE_" + user.getRole().toString();
    if (compareRole(callerRole, targetRole) <= 0) {
      Map<String, Object> violations = new HashMap<>();
      violations.put("role", "You don't have the authorities to perform this action");
      throw new BadRequestDetailsException(violations);
    }

    tokenRepository.deleteTokenByUser(user);
    userRepository.deleteUserById(id);
  }

  @Override
  @Transactional
  public void deleteUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = userRepository.findUserByEmail(authentication.getName()).orElseThrow(() -> {
      Map<String, Object> violations = new HashMap<>();
      violations.put("id", "No user with username " + authentication.getName() + " found");
      return new BadRequestDetailsException(violations);
    });

    tokenRepository.deleteTokenByUser(user);
    userRepository.deleteUserByEmail(authentication.getName());
    SecurityContextHolder.getContext().setAuthentication(null);
  }
}
