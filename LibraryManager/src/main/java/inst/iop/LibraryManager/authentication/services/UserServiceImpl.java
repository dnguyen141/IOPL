package inst.iop.LibraryManager.authentication.services;

import inst.iop.LibraryManager.authentication.dtos.UpdateDetailsDto;
import inst.iop.LibraryManager.authentication.dtos.UpdateUserDetailsDto;
import inst.iop.LibraryManager.authentication.dtos.RegisterDto;
import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.authentication.entities.enums.Role;
import inst.iop.LibraryManager.authentication.repositories.TokenRepository;
import inst.iop.LibraryManager.authentication.repositories.UserRepository;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static inst.iop.LibraryManager.utilities.ConstraintViolationSetHandler.convertSetToMap;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  private final TokenRepository tokenRepository;

  private final PasswordEncoder passwordEncoder;

  private final Validator validator;

  @Override
  public User findUserById(Long id) throws BadRequestDetailsException {
    User user = userRepository.findUserById(id).orElseThrow(() -> {
      Map<String, String> violations = new HashMap<>();
      violations.put("id", "There is no user with id " + id);
      return new BadRequestDetailsException("Unable to get user with id " + id, violations);
    });

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String callerRole = authentication.getAuthorities().stream().toList().get(0).toString();
    String targetRole = "ROLE_" + user.getRole().toString();

    if (compareRole(callerRole, targetRole) <= 0) {
      Map<String, String> violations = new HashMap<>();
      violations.put("role", "You are not allowed to perform this action");
      throw new BadRequestDetailsException("Unable to get user with id " + id, violations);
    }

    return user;
  }

  @Override
  public User findUserByEmail(String email) throws BadRequestDetailsException {
    return userRepository.findUserByEmail(email).orElseThrow(() -> {
      Map<String, String> violations = new HashMap<>();
      violations.put("email", "There is no user with email address " + email);
      return new BadRequestDetailsException("Unable to find user by email", violations);
    });
  }

  @Override
  public Page<User> findAllModeratorsAndUsers(Integer pageNumber, Integer pageSize) {
    return userRepository.findAllModeratorsAndUsers(PageRequest.of(pageNumber, pageSize));
  }

  @Override
  public Page<User> findAllUsers(Integer pageNumber, Integer pageSize) {
    return userRepository.findAllUsers(PageRequest.of(pageNumber, pageSize));
  }

  @Override
  @Transactional
  public User createUser(RegisterDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertSetToMap(validator.validate(request));

    if (checkInvalidConfirmedPassword(request.getPassword(), request.getConfirmedPassword())) {
      violations.put("confirmedPassword", "Password and confirmed password must be matched");
    }

    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid user register request", violations);
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String callerRole = authentication.getAuthorities().stream().toList().get(0).toString();
    if (compareRole(callerRole, "ROLE_USER") <= 0) {
      violations.put("role", "You are not permitted to create an user that has more privileges that you");
      throw new BadRequestDetailsException("Invalid user register request", violations);
    }

    Optional<User> u = userRepository.findUserByEmail(request.getEmail());
    if (u.isPresent()) {
      violations.put("email", "An user with the same email is already existed");
      throw new BadRequestDetailsException("Invalid user register request", violations);
    }

    User user = User.builder()
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .createdDate(LocalDate.now())
        .role(Role.USER)
        .enabled(true)
        .build();
    userRepository.save(user);
    return user;
  }

  @Override
  @Transactional
  public void updateOtherUserById(Long id, UpdateUserDetailsDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertSetToMap(validator.validate(request));

    if (checkInvalidConfirmedPassword(request.getPassword(), request.getConfirmedPassword())) {
      violations.put("confirmedPassword", "Password and confirmed password must be matched");
    }

    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid update other user request", violations);
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String callerRole = authentication.getAuthorities().stream().toList().get(0).toString();

    User user = userRepository.findUserById(id).orElseThrow(() -> {
      violations.put("id", "No user with id " + id + " found");
      return new BadRequestDetailsException("Invalid update other user request", violations);
    });

    String targetRole = "ROLE_" + user.getRole().toString();
    if (compareRole(callerRole, targetRole) <= 0) {
      violations.put("role", "You are not allowed to perform this action");
      throw new BadRequestDetailsException("Invalid update other user request", violations);
    }

    userRepository.updateUserById(
        id,
        request.getPassword() != null && !passwordEncoder.encode(request.getPassword()).equals(user.getPassword()) ?
            passwordEncoder.encode(request.getPassword()) : user.getPassword(),
        request.getFirstName(),
        request.getLastName(),
        Role.getRoleFromString(request.getRole())
    );
  }

  @Override
  @Transactional
  public void updateUserByEmail(UpdateDetailsDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertSetToMap(validator.validate(request));

    if (checkInvalidConfirmedPassword(request.getPassword(), request.getConfirmedPassword())) {
      violations.put("confirmedPassword", "Password and confirmed password must be matched");
    }

    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid update user request", violations);
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = userRepository.findUserByEmail(authentication.getName()).orElseThrow(() -> {
      violations.put("email", "No user with email " + authentication.getName() + " found");
      return new BadRequestDetailsException("Invalid update user request", violations);
    });

    userRepository.updateUserByEmail(
        authentication.getName(),
        request.getPassword() != null && !passwordEncoder.encode(request.getPassword()).equals(user.getPassword()) ?
            passwordEncoder.encode(request.getPassword()) : user.getPassword(),
        request.getFirstName(),
        request.getLastName(),
        user.getRole()
    );
  }

  public int compareRole(String callerRole, String targetRole) throws IllegalArgumentException {
    return Integer.compare(
        Objects.requireNonNull(Role.getRoleFromAuthority(callerRole)).getOrder(),
        Objects.requireNonNull(Role.getRoleFromAuthority(targetRole)).getOrder()
    );
  }

  public boolean checkInvalidConfirmedPassword(String password, String confirmedPassword) {
    return !password.equals(confirmedPassword);
  }

  @Override
  @Transactional
  public void deleteUserById(Long id) throws BadRequestDetailsException {
    User user = userRepository.findUserById(id).orElseThrow(() -> {
      Map<String, String> violations = new HashMap<>();
      violations.put("id", "No user with id " + id + " found");
      return new BadRequestDetailsException("Invalid delete user request", violations);
    });

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String callerRole = authentication.getAuthorities().stream().toList().get(0).toString();
    String targetRole = "ROLE_" + user.getRole().toString();
    if (compareRole(callerRole, targetRole) <= 0) {
      Map<String, String> violations = new HashMap<>();
      violations.put("role", "You don't have the authorities to perform this action");
      throw new BadRequestDetailsException("Invalid delete user request", violations);
    }

    tokenRepository.deleteTokenByUser(user);
    userRepository.deleteUserById(id);
  }

  @Override
  @Transactional
  public void deleteUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = userRepository.findUserByEmail(authentication.getName()).orElseThrow(() -> {
      Map<String, String> violations = new HashMap<>();
      violations.put("id", "No user with username " + authentication.getName() + " found");
      return new BadRequestDetailsException("Invalid delete user request", violations);
    });

    tokenRepository.deleteTokenByUser(user);
    userRepository.deleteUserByEmail(authentication.getName());
    SecurityContextHolder.getContext().setAuthentication(null);
  }
}
