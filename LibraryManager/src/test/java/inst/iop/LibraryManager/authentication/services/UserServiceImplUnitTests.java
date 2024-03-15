package inst.iop.LibraryManager.authentication.services;

import inst.iop.LibraryManager.authentication.dtos.UpdateDetailsDto;
import inst.iop.LibraryManager.authentication.dtos.UpdateUserDetailsDto;
import inst.iop.LibraryManager.authentication.dtos.RegisterDto;
import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.authentication.entities.enums.Role;
import inst.iop.LibraryManager.authentication.repositories.TokenRepository;
import inst.iop.LibraryManager.authentication.repositories.UserRepository;
import inst.iop.LibraryManager.authentication.services.UserServiceImpl;
import inst.iop.LibraryManager.utilities.ConstraintViolationSetHandler;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import io.jsonwebtoken.lang.Collections;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
public class UserServiceImplUnitTests {

  @Mock
  private UserRepository userRepository;

  @Mock
  private TokenRepository tokenRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private Validator validator;

  @Mock
  private UsernamePasswordAuthenticationToken authentication;

  @Mock
  private SecurityContextImpl securityContext;

  @InjectMocks
  private UserServiceImpl userService;

  private User user;

  private User moderator;

  private static final int mockPageNumber = 0;

  private static final int mockPageSize = 10;

  @BeforeEach
  public void setUp() {
    user = User.builder()
        .id(0L)
        .email("user@email.com")
        .password("testEncodedPassword")
        .firstName("User")
        .lastName("Name")
        .role(Role.USER)
        .createdDate(LocalDate.now())
        .enabled(false)
        .build();

    moderator = User.builder()
        .id(1L)
        .email("mod@email.com")
        .password("testEncodedPassword")
        .firstName("Moderator")
        .lastName("Name")
        .role(Role.MODERATOR)
        .createdDate(LocalDate.now())
        .enabled(false)
        .build();
  }

  @Test
  public void testFindUserByIdHappyFlow() {
    log.info("Test running - Find user by id happy flow...");

    when(userRepository.findUserById(0L)).thenReturn(Optional.of(user));

    try (
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class)
    ) {
      sch.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getAuthorities()).thenReturn(Collections.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

      assertEquals(user, userService.findUserById(0L));

      verify(userRepository, times(1)).findUserById(0L);
      sch.verify(SecurityContextHolder::getContext, times(1));
      verify(securityContext, times(1)).getAuthentication();
      verify(authentication, times(1)).getAuthorities();
    }

    log.info("Passed - Find user by id happy flow");
  }

  @Test
  public void testFindUserByIdUserNotFoundFlow() {
    log.info("Test running - Find user by id when user is not found...");

    when(userRepository.findUserById(0L)).thenReturn(Optional.empty());

    try (
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class)
    ) {
      BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
          () -> userService.findUserById(0L)
      );
      assertEquals("Unable to get user with id 0", exception.getMessage());
      Map<String, String> violations = new HashMap<>();
      violations.put("id", "There is no user with id 0");
      assertEquals(violations, exception.getViolations());

      verify(userRepository, times(1)).findUserById(0L);
      sch.verify(SecurityContextHolder::getContext, never());
      verify(securityContext, never()).getAuthentication();
      verify(authentication, never()).getAuthorities();
    }

    log.info("Passed - Find user by id when user is not found");
  }

  @Test
  public void testFindUserByIdUnauthorizedFlow() {
    log.info("Test running - Find user by id when user sent the request doesn't have authority...");

    when(userRepository.findUserById(0L)).thenReturn(Optional.of(user));

    try (
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class)
    ) {
      sch.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getAuthorities()).thenReturn(Collections.of(new SimpleGrantedAuthority("ROLE_USER")));

      BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
          () -> userService.findUserById(0L)
      );
      assertEquals("Unable to get user with id 0", exception.getMessage());
      Map<String, String> violations = new HashMap<>();
      violations.put("role", "You are not allowed to perform this action");
      assertEquals(violations, exception.getViolations());

      verify(userRepository, times(1)).findUserById(0L);
      sch.verify(SecurityContextHolder::getContext, times(1));
      verify(securityContext, times(1)).getAuthentication();
      verify(authentication, times(1)).getAuthorities();
    }

    log.info("Passed - Find user by id when user sent the request doesn't have authority");
  }

  @Test
  public void testFindUserByEmailHappyFlow() {
    log.info("Test running - Find user by email happy flow...");

    when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));

    assertEquals(user, userService.findUserByEmail(user.getEmail()));

    verify(userRepository, times(1)).findUserByEmail(user.getEmail());

    log.info("Passed - Find user by email happy flow");
  }

  @Test
  public void testFindUserByEmailUserNotFoundFlow() {
    log.info("Test running - Find user by email when user can't be found...");

    when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.empty());

    BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
        () -> userService.findUserByEmail(user.getEmail())
    );
    assertEquals("Unable to find user by email", exception.getMessage());
    Map<String, String> violations = new HashMap<>();
    violations.put("email", "There is no user with email address " + user.getEmail());
    assertEquals(violations, exception.getViolations());

    verify(userRepository, times(1)).findUserByEmail(user.getEmail());

    log.info("Passed - Find user by email when user can't be found");
  }

  @Test
  public void testFindAllModeratorsAndUsersHappyFlow() {
    PageImpl<User> expected = new PageImpl<>(List.of(user, moderator));

    when(userRepository.findAllModeratorsAndUsers(PageRequest.of(mockPageNumber, mockPageSize))).thenReturn(expected);

    Page<User> result = userService.findAllModeratorsAndUsers(mockPageNumber, mockPageSize);
    assertEquals(expected, result);
  }

  @Test
  public void testFindAllUsers() {
    PageImpl<User> expected = new PageImpl<>(List.of(user));

    when(userRepository.findAllUsers(PageRequest.of(mockPageNumber, mockPageSize))).thenReturn(expected);

    Page<User> result = userService.findAllUsers(mockPageNumber, mockPageSize);
    assertEquals(expected, result);
  }

  @Test
  public void testCreateUserHappyFlow() {
    log.info("Test running - Create user happy flow...");

    user.setEnabled(true);

    RegisterDto request = RegisterDto.builder()
        .email("user@email.com")
        .password("testPassword")
        .confirmedPassword("testPassword")
        .firstName("User")
        .lastName("Name")
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class);
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      sch.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getAuthorities()).thenReturn(Collections.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
      when(userRepository.findUserByEmail(request.getEmail())).thenReturn(Optional.empty());
      when(passwordEncoder.encode(request.getPassword())).thenReturn("testEncodedPassword");

      assertEquals(user, userService.createUser(request));

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      sch.verify(SecurityContextHolder::getContext, times(1));
      verify(securityContext, times(1)).getAuthentication();
      verify(userRepository, times(1)).findUserByEmail(request.getEmail());
      verify(userRepository, times(1)).save(user);
    }

    log.info("Passed - Create user happy flow");
  }

  @Test
  public void testCreateUserInvalidInputsFlow() {
    log.info("Test running - Create user when inputs are invalid...");

    user.setEnabled(true);

    RegisterDto request = RegisterDto.builder()
        .email("user$email.com")
        .password("testWrongPassword")
        .confirmedPassword("testPassword")
        .firstName("")
        .lastName("")
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class);
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(getViolationsList());

      BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
          () -> userService.createUser(request));
      assertEquals(exception.getMessage(), "Invalid user register request");
      assertEquals(exception.getViolations(), getViolationsList());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      sch.verify(SecurityContextHolder::getContext, never());
      verify(securityContext, never()).getAuthentication();
      verify(userRepository, never()).findUserByEmail(request.getEmail());
      verify(userRepository, never()).save(any());
    }

    log.info("Passed - Create user when inputs are invalid");
  }

  private static Map<String, String> getViolationsList() {
    Map<String, String> violations = new HashMap<>();
    violations.put("firstName", "Name must be at least 1 and at most 50 characters");
    violations.put("lastName", "Name must be at least 1 and at most 50 characters");
    violations.put("password", "The password must contain at least one lowercase Latin character and a " +
        "length of at least and at most 20 characters");
    violations.put("confirmedPassword", "Password and confirmed password must be matched");
    violations.put("email", "The email address is either invalid or used");
    return violations;
  }

  @Test
  public void testCreateUserInvalidAuthorityFlow() {
    log.info("Test running - Create user when user who sends the request doesn't have authority...");

    RegisterDto request = RegisterDto.builder()
        .email("user@email.com")
        .password("testPassword")
        .confirmedPassword("testPassword")
        .firstName("User")
        .lastName("Name")
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class);
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      sch.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getAuthorities()).thenReturn(Collections.of(new SimpleGrantedAuthority("ROLE_USER")));

      BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
          () -> userService.createUser(request));
      assertEquals("Invalid user register request", exception.getMessage());

      Map<String, String> violations = new HashMap<>();
      violations.put("role", "You are not permitted to create an user that has more privileges that you");
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      sch.verify(SecurityContextHolder::getContext, times(1));
      verify(securityContext, times(1)).getAuthentication();
      verify(userRepository, never()).findUserByEmail(request.getEmail());
      verify(userRepository, never()).save(any());
    }

    log.info("Passed - Create user when user who sends the request doesn't have authority");
  }

  @Test
  public void testCreateUserEmailAlreadyExistedFlow() {
    log.info("Test running - Create user when new user's email is already used...");

    RegisterDto request = RegisterDto.builder()
        .email("user@email.com")
        .password("testPassword")
        .confirmedPassword("testPassword")
        .firstName("User")
        .lastName("Name")
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class);
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      sch.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getAuthorities()).thenReturn(Collections.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
      when(userRepository.findUserByEmail(request.getEmail())).thenReturn(Optional.of(user));

      BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
          () -> userService.createUser(request));
      assertEquals("Invalid user register request", exception.getMessage());

      Map<String, String> violations = new HashMap<>();
      violations.put("email", "An user with the same email is already existed");
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      sch.verify(SecurityContextHolder::getContext, times(1));
      verify(securityContext, times(1)).getAuthentication();
      verify(userRepository, times(1)).findUserByEmail(request.getEmail());
      verify(userRepository, never()).save(any());
    }

    log.info("Passed - Create user when new user's email is already used...");
  }

  @Test
  public void testUpdateOtherUserByIdHappyFlow() {
    log.info("Test running - Update other user happy flow...");

    user.setEnabled(true);

    UpdateUserDetailsDto request = UpdateUserDetailsDto.builder()
        .password("testNewPassword")
        .confirmedPassword("testNewPassword")
        .firstName("NewUser")
        .lastName("NewName")
        .role("MODERATOR")
        .build();

    User updatedUser = User.builder()
        .id(0L)
        .email("user@email.com")
        .password("testNewEncodedPassword")
        .firstName("NewUser")
        .lastName("NewName")
        .role(Role.MODERATOR)
        .createdDate(user.getCreatedDate())
        .enabled(true)
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class);
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      sch.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getAuthorities()).thenReturn(Collections.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
      when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(user));
      when(passwordEncoder.encode(request.getPassword())).thenReturn("testNewEncodedPassword");

      userService.updateOtherUserById(user.getId(), request);

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      sch.verify(SecurityContextHolder::getContext, times(1));
      verify(securityContext, times(1)).getAuthentication();
      verify(userRepository, times(1)).findUserById(updatedUser.getId());
      verify(userRepository, times(1)).updateUserById(
          updatedUser.getId(),
          updatedUser.getPassword(),
          updatedUser.getFirstName(),
          updatedUser.getLastName(),
          updatedUser.getRole()
      );
    }

    log.info("Passed - Update other user happy flow");
  }

  @Test
  public void testUpdateOtherUserByIdInvalidInputsFlow() {
    log.info("Test running - Update other user when inputs are invalid...");

    user.setEnabled(true);

    UpdateUserDetailsDto request = UpdateUserDetailsDto.builder()
        .password("testWrongNewPassword")
        .confirmedPassword("testNewPassword")
        .firstName("NewUser")
        .lastName("NewName")
        .role("MODERATOR")
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class);
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(getViolationsList());

      BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
          () -> userService.updateOtherUserById(user.getId(), request));
      assertEquals("Invalid update other user request", exception.getMessage());
      assertEquals(getViolationsList(), exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      sch.verify(SecurityContextHolder::getContext, never());
      verify(securityContext, never()).getAuthentication();
      verify(userRepository, never()).findUserById(any());
      verify(userRepository, never()).updateUserById(any(), any(), any(), any(), any());
    }

    log.info("Passed - Update other user when inputs are invalid");
  }

  @Test
  public void testUpdateOtherUserByIdUserNotFoundFlow() {
    log.info("Test running - Update other user when the other user can't be found...");

    user.setEnabled(true);

    UpdateUserDetailsDto request = UpdateUserDetailsDto.builder()
        .password("testNewPassword")
        .confirmedPassword("testNewPassword")
        .firstName("NewUser")
        .lastName("NewName")
        .role("MODERATOR")
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class);
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      sch.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getAuthorities()).thenReturn(Collections.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
      when(userRepository.findUserById(user.getId())).thenReturn(Optional.empty());

      BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
          () -> userService.updateOtherUserById(user.getId(), request));
      assertEquals("Invalid update other user request", exception.getMessage());
      Map<String, String> violations = new HashMap<>();
      violations.put("id", "No user with id 0 found");
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      sch.verify(SecurityContextHolder::getContext, times(1));
      verify(securityContext, times(1)).getAuthentication();
      verify(userRepository, times(1)).findUserById(user.getId());
      verify(userRepository, never()).updateUserById(any(), any(), any(), any(), any());
    }

    log.info("Passed - Update other user when the other user can't be found");
  }

  @Test
  public void testUpdateOtherUserByIdUnauthorizedFlow() {
    log.info("Test running - Update other user when the user requested update doesn't have authority...");

    user.setEnabled(true);

    UpdateUserDetailsDto request = UpdateUserDetailsDto.builder()
        .password("testNewPassword")
        .confirmedPassword("testNewPassword")
        .firstName("NewUser")
        .lastName("NewName")
        .role("MODERATOR")
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class);
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      sch.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getAuthorities()).thenReturn(Collections.of(new SimpleGrantedAuthority("ROLE_USER")));
      when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(user));

      BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
          () -> userService.updateOtherUserById(user.getId(), request));
      assertEquals("Invalid update other user request", exception.getMessage());
      Map<String, String> violations = new HashMap<>();
      violations.put("role", "You are not allowed to perform this action");
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      sch.verify(SecurityContextHolder::getContext, times(1));
      verify(securityContext, times(1)).getAuthentication();
      verify(userRepository, times(1)).findUserById(any());
      verify(userRepository, never()).updateUserById(any(), any(), any(), any(), any());
    }

    log.info("Passed - Update other user when the user requested update doesn't have authority");
  }

  @Test
  public void testUpdateUserByEmailHappyFlow() {
    log.info("Test running - Update current user happy flow...");

    user.setEnabled(true);

    UpdateDetailsDto request = UpdateDetailsDto.builder()
        .password("testNewPassword")
        .confirmedPassword("testNewPassword")
        .firstName("NewUser")
        .lastName("NewName")
        .build();

    User updatedUser = User.builder()
        .id(0L)
        .email("user@email.com")
        .password("testNewEncodedPassword")
        .firstName("NewUser")
        .lastName("NewName")
        .role(Role.USER)
        .createdDate(user.getCreatedDate())
        .enabled(true)
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class);
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      sch.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn(user.getEmail());
      when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
      when(passwordEncoder.encode(request.getPassword())).thenReturn("testNewEncodedPassword");

      userService.updateUserByEmail(request);

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      sch.verify(SecurityContextHolder::getContext, times(1));
      verify(securityContext, times(1)).getAuthentication();
      verify(userRepository, times(1)).findUserByEmail(user.getEmail());
      verify(userRepository, times(1)).updateUserByEmail(
          user.getEmail(),
          updatedUser.getPassword(),
          updatedUser.getFirstName(),
          updatedUser.getLastName(),
          updatedUser.getRole()
      );
    }

    log.info("Passed - Update current user happy flow");
  }

  @Test
  public void testUpdateUserByEmailInvalidInputsFlow() {
    log.info("Test running - Update current user when inputs are invalid...");

    user.setEnabled(true);

    UpdateDetailsDto request = UpdateDetailsDto.builder()
        .password("testWrongNewPassword")
        .confirmedPassword("testNewPassword")
        .firstName("")
        .lastName("")
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class);
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class)
    ) {
      Map<String, String> violations = new HashMap<>();
      violations.put("firstName", "Name must be at least 1 and at most 50 characters");
      violations.put("lastName", "Name must be at least 1 and at most 50 characters");
      violations.put("password", "The password must contain at least one lowercase Latin character and a " +
          "length of at least and at most 20 characters");
      violations.put("confirmedPassword", "Password and confirmed password must be matched");
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(violations);

      BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
          () -> userService.updateUserByEmail(request));
      assertEquals("Invalid update user request", exception.getMessage());
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      sch.verify(SecurityContextHolder::getContext, never());
      verify(securityContext, never()).getAuthentication();
      verify(userRepository, never()).findUserByEmail(user.getEmail());
      verify(userRepository, never()).updateUserByEmail(any(), any(), any(), any(), any());
    }

    log.info("Passed - Update current user when inputs are invalid");
  }

  @Test
  public void testUpdateUserByEmailUserNotFoundFlow() {
    log.info("Test running - Update current user when user can't be found...");

    user.setEnabled(true);

    UpdateDetailsDto request = UpdateDetailsDto.builder()
        .password("testNewPassword")
        .confirmedPassword("testNewPassword")
        .firstName("NewUser")
        .lastName("NewName")
        .build();

    try (
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class);
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      sch.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn(user.getEmail());
      when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.empty());

      BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
          () -> userService.updateUserByEmail(request));
      assertEquals("Invalid update user request", exception.getMessage());
      Map<String, String> violations = new HashMap<>();
      violations.put("email", "No user with email " + authentication.getName() + " found");
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      sch.verify(SecurityContextHolder::getContext, times(1));
      verify(securityContext, times(1)).getAuthentication();
      verify(userRepository, times(1)).findUserByEmail(user.getEmail());
      verify(userRepository, never()).updateUserByEmail(any(), any(), any(), any(), any());
    }

    log.info("Passed - Update current user when user can't be found");
  }

  @Test
  public void testDeleteUserByIdHappyFlow() {
    log.info("Test running - Delete user by id happy flow...");

    try (
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class)
    ) {
      when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(user));
      sch.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getAuthorities()).thenReturn(Collections.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

      userService.deleteUserById(user.getId());

      verify(userRepository, times(1)).findUserById(user.getId());
      sch.verify(SecurityContextHolder::getContext, times(1));
      verify(securityContext, times(1)).getAuthentication();
      verify(authentication, times(1)).getAuthorities();
      verify(tokenRepository, times(1)).deleteTokenByUser(user);
      verify(userRepository, times(1)).deleteUserById(user.getId());
    }

    log.info("Passed - Delete user by id happy flow");
  }

  @Test
  public void testDeleteUserByIdUserNotFoundFlow() {
    log.info("Test running - Delete user by id when user can't be found...");

    try (
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class)
    ) {
      when(userRepository.findUserById(user.getId())).thenReturn(Optional.empty());

      BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
          () -> userService.deleteUserById(user.getId())
      );
      assertEquals("Invalid delete user request", exception.getMessage());
      Map<String, String> violations = new HashMap<>();
      violations.put("id", "No user with id " + user.getId() + " found");
      assertEquals(violations, exception.getViolations());

      verify(userRepository, times(1)).findUserById(user.getId());
      sch.verify(SecurityContextHolder::getContext, never());
      verify(securityContext, never()).getAuthentication();
      verify(authentication, never()).getAuthorities();
      verify(tokenRepository, never()).deleteTokenByUser(user);
      verify(userRepository, never()).deleteUserById(user.getId());
    }

    log.info("Passed - Delete user by id when user can't be found");
  }

  @Test
  public void testDeleteUserByIdUnauthorizedFlow() {
    log.info("Test running - Delete user by id when requested user doesn't have authority...");

    try (
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class)
    ) {
      when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(user));
      sch.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getAuthorities()).thenReturn(Collections.of(new SimpleGrantedAuthority("ROLE_USER")));

      BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
          () -> userService.deleteUserById(user.getId())
      );
      assertEquals("Invalid delete user request", exception.getMessage());
      Map<String, String> violations = new HashMap<>();
      violations.put("role", "You don't have the authorities to perform this action");
      assertEquals(violations, exception.getViolations());

      verify(userRepository, times(1)).findUserById(user.getId());
      sch.verify(SecurityContextHolder::getContext, times(1));
      verify(securityContext, times(1)).getAuthentication();
      verify(authentication, times(1)).getAuthorities();
      verify(tokenRepository, never()).deleteTokenByUser(user);
      verify(userRepository, never()).deleteUserById(user.getId());
    }

    log.info("Passed - Delete user by id when requested user doesn't have authority");
  }

  @Test
  public void testDeleteUserHappyFlow() {
    log.info("Test running - Delete current user happy flow...");

    try (
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class)
    ) {
      sch.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn(user.getEmail());
      when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));

      userService.deleteUser();

      verify(userRepository, times(1)).findUserByEmail(user.getEmail());
      sch.verify(SecurityContextHolder::getContext, times(2));
      verify(securityContext, times(1)).getAuthentication();
      verify(tokenRepository, times(1)).deleteTokenByUser(user);
      verify(userRepository, times(1)).deleteUserByEmail(user.getEmail());
      verify(securityContext, times(1)).setAuthentication(null);
    }

    log.info("Passed - Delete current user happy flow");
  }

  @Test
  public void testDeleteUserUserNotFoundFlow() {
    log.info("Test running - Delete current user when user can't be found...");

    try (
        MockedStatic<SecurityContextHolder> sch = mockStatic(SecurityContextHolder.class)
    ) {
      sch.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn(user.getEmail());
      when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.empty());

      BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
          () -> userService.deleteUser()
      );
      assertEquals("Invalid delete user request", exception.getMessage());
      Map<String, String> violations = new HashMap<>();
      violations.put("id", "No user with username " + user.getEmail() + " found");
      assertEquals(violations, exception.getViolations());

      verify(userRepository, times(1)).findUserByEmail(user.getEmail());
      sch.verify(SecurityContextHolder::getContext, times(1));
      verify(securityContext, times(1)).getAuthentication();
      verify(tokenRepository, never()).deleteTokenByUser(user);
      verify(userRepository, never()).deleteUserByEmail(user.getEmail());
      verify(securityContext, never()).setAuthentication(null);
    }

    log.info("Passed - Delete current user when user can't be found");
  }
}
