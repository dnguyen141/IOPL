package inst.iop.LibraryManager.authentication.services;

import inst.iop.LibraryManager.authentication.dtos.LoginDto;
import inst.iop.LibraryManager.authentication.dtos.RegisterDto;
import inst.iop.LibraryManager.authentication.entities.JwtToken;
import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.authentication.entities.enums.Role;
import inst.iop.LibraryManager.authentication.entities.enums.TokenType;
import inst.iop.LibraryManager.authentication.repositories.TokenRepository;
import inst.iop.LibraryManager.authentication.repositories.UserRepository;
import inst.iop.LibraryManager.authentication.services.*;
import inst.iop.LibraryManager.utilities.ConfirmationCodeGenerator;
import inst.iop.LibraryManager.utilities.ConstraintViolationSetHandler;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
public class AuthenticationServiceImplUnitTests {

  @Mock
  private UserRepository userRepository;

  @Mock
  private TokenRepository tokenRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private Validator validator;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private EmailServiceImpl emailService;

  @Mock
  private JwtServiceImpl jwtService;

  @InjectMocks
  private AuthenticationServiceImpl authenticationService;

  private static final String mockConfirmationCode = "testConfirmationCode";

  private User newUser;

  @BeforeEach
  public void setUp() {

    newUser = User.builder()
        .email("newUser@email.com")
        .password("testEncodedPassword")
        .firstName("New")
        .lastName("User")
        .role(Role.USER)
        .borrowEntries(new HashSet<>())
        .createdDate(LocalDate.now())
        .enabled(false)
        .confirmationCode(mockConfirmationCode)
        .build();
  }

  @Test
  public void testRegisterHappyFlow() {
    log.info("Test running - Register service happy flow...");

    RegisterDto request = RegisterDto.builder()
        .email("newUser@email.com")
        .password("testPassword")
        .confirmedPassword("testPassword")
        .firstName("New")
        .lastName("User")
        .build();

    String mockConfirmationCode = "testConfirmationCode";

    try (
        MockedStatic<ConfirmationCodeGenerator> ccg = mockStatic(ConfirmationCodeGenerator.class);
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(userRepository.findUserByEmail(request.getEmail())).thenReturn(Optional.empty());
      ccg.when(ConfirmationCodeGenerator::generateSecuredUuid).thenReturn(mockConfirmationCode);
      when(passwordEncoder.encode(request.getPassword())).thenReturn("testEncodedPassword");
      when(userRepository.save(newUser)).thenReturn(newUser);
      when(emailService.sendConfirmationEmailToRecipient(
          "minhdinhnguyen1495@gmail.com", request.getFirstName(), request.getLastName(), mockConfirmationCode
      )).thenReturn(true);

      User user = authenticationService.register(request);
      assertEquals(newUser, user);

      verify(userRepository, times(1)).findUserByEmail(request.getEmail());
      ccg.verify(ConfirmationCodeGenerator::generateSecuredUuid, times(1));
      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(userRepository, times(1)).save(newUser);
      verify(emailService, times(1)).sendConfirmationEmailToRecipient(
          "minhdinhnguyen1495@gmail.com", request.getFirstName(), request.getLastName(), mockConfirmationCode
      );
    } catch (IOException e) {
      fail("Email is assumed to be successfully sent. This shouldn't be happened.");
    }

    log.info("Passed - Register service happy flow");
  }

  @Test
  public void testRegisterUserExistedExceptionFlow() {
    log.info("Test running - Register service when user with same email existed...");

    RegisterDto request = RegisterDto.builder()
        .email("newUser@email.com")
        .password("testPassword")
        .confirmedPassword("testPassword")
        .firstName("New")
        .lastName("User")
        .build();

    try (
        MockedStatic<ConfirmationCodeGenerator> ccg = mockStatic(ConfirmationCodeGenerator.class);
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(userRepository.findUserByEmail(request.getEmail())).thenReturn(Optional.of(newUser));
      BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
          () -> authenticationService.register(request));
      assertEquals("Invalid register request", exception.getMessage());
      Map<String, String> violations = new HashMap<>();
      violations.put("email", "An user with the same email is already existed");
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(userRepository, times(1)).findUserByEmail(request.getEmail());
      ccg.verify(ConfirmationCodeGenerator::generateSecuredUuid, never());
      verify(userRepository, never()).save(any());
      verify(emailService, never()).sendConfirmationEmailToRecipient(
          anyString(), anyString(), anyString(), anyString()
      );
    } catch (IOException e) {
      fail("Email is assumed not to be sent. This shouldn't be happened.");
    }

    log.info("Passed - Register service when user with same email existed...");
  }

  @Test
  public void testRegisterInvalidInputsExceptionFlow() {
    log.info("Test running - Register service when inputs are invalid...");

    RegisterDto request = RegisterDto.builder()
        .email("newUser$email.com")
        .password("123456")
        .confirmedPassword("1234567")
        .firstName("")
        .lastName("")
        .build();

    try (
        MockedStatic<ConfirmationCodeGenerator> ccg = mockStatic(ConfirmationCodeGenerator.class);
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(getViolationsList());
      BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
          () -> authenticationService.register(request));
      assertEquals("Invalid register request", exception.getMessage());
      assertEquals(getViolationsList(), exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(userRepository, never()).findUserByEmail(anyString());
      ccg.verify(ConfirmationCodeGenerator::generateSecuredUuid, never());
      verify(userRepository, never()).save(any());
      verify(emailService, never()).sendConfirmationEmailToRecipient(
          anyString(), anyString(), anyString(), anyString()
      );
    } catch (IOException e) {
      fail("Email is assumed not to be sent. This shouldn't be happened.");
    }

    log.info("Passed - Register service when inputs are invalid");
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
  public void testRegisterFailToSendConfirmationEmailFlow() {
    log.info("Test running - Register service when confirmation email can't be sent...");

    RegisterDto request = RegisterDto.builder()
        .email("newUser@email.com")
        .password("testPassword")
        .confirmedPassword("testPassword")
        .firstName("New")
        .lastName("User")
        .build();

    try (
        MockedStatic<ConfirmationCodeGenerator> ccg = mockStatic(ConfirmationCodeGenerator.class);
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(userRepository.findUserByEmail(request.getEmail())).thenReturn(Optional.empty());
      ccg.when(ConfirmationCodeGenerator::generateSecuredUuid).thenReturn(mockConfirmationCode);
      when(emailService.sendConfirmationEmailToRecipient(
          "minhdinhnguyen1495@gmail.com", request.getFirstName(), request.getLastName(), mockConfirmationCode
      )).thenReturn(false);

      BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
          () -> authenticationService.register(request));

      assertEquals("Invalid register request", exception.getMessage());
      Map<String, String> violations = new HashMap<>();
      violations.put("email", "Unable to send confirmation email");
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(userRepository, times(1)).findUserByEmail(request.getEmail());
      ccg.verify(ConfirmationCodeGenerator::generateSecuredUuid, times(1));
      verify(emailService, times(1)).sendConfirmationEmailToRecipient(
          "minhdinhnguyen1495@gmail.com", request.getFirstName(), request.getLastName(), mockConfirmationCode
      );
      verify(userRepository, never()).save(any());
    } catch (IOException e) {
      fail("Email is assumed not to be sent. This shouldn't be happened.");
    }

    log.info("Passed - Register service when confirmation email can't be sent");
  }

  @Test
  public void testRegisterIOExceptionFlow() {
    log.info("Test running - Register service when IOException is raised during sending confirmation email...");

    RegisterDto request = RegisterDto.builder()
        .email("newUser@email.com")
        .password("testPassword")
        .confirmedPassword("testPassword")
        .firstName("New")
        .lastName("User")
        .build();

    try (
        MockedStatic<ConfirmationCodeGenerator> ccg = mockStatic(ConfirmationCodeGenerator.class);
        MockedStatic<ConstraintViolationSetHandler> cvs = mockStatic(ConstraintViolationSetHandler.class)
    ) {
      when(ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)))
          .thenReturn(new HashMap<>());
      when(userRepository.findUserByEmail(request.getEmail())).thenReturn(Optional.empty());
      ccg.when(ConfirmationCodeGenerator::generateSecuredUuid).thenReturn(mockConfirmationCode);
      when(emailService.sendConfirmationEmailToRecipient(
          "minhdinhnguyen1495@gmail.com", request.getFirstName(), request.getLastName(), mockConfirmationCode
      )).thenThrow(IOException.class);

      BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
          () -> authenticationService.register(request));

      assertEquals("Invalid register request", exception.getMessage());

      Map<String, String> violations = new HashMap<>();
      violations.put("email", "Unable to send confirmation email");
      assertEquals(violations, exception.getViolations());

      cvs.verify(() -> ConstraintViolationSetHandler.convertSetToMap(validator.validate(request)),
          times(1));
      verify(userRepository, times(1)).findUserByEmail(request.getEmail());
      ccg.verify(ConfirmationCodeGenerator::generateSecuredUuid, times(1));
      verify(emailService, times(1)).sendConfirmationEmailToRecipient(
          "minhdinhnguyen1495@gmail.com", request.getFirstName(), request.getLastName(), mockConfirmationCode
      );
      verify(userRepository, never()).save(any());
    } catch (IOException e) {
      fail("Email is assumed not to be sent. This shouldn't be happened.");
    }

    log.info("Passed - Register service when IOException is raised during sending confirmation email");
  }

  @Test
  public void testLoginHappyFlow() {
    log.info("Test running - Login service happy flow...");

    newUser.setEnabled(true);
    when(userRepository.findUserByEmail(newUser.getEmail())).thenReturn(Optional.of(newUser));

    LoginDto request = LoginDto.builder()
        .email("newUser@email.com")
        .password("testPassword")
        .build();

    var oldAccessToken = JwtToken.builder()
        .user(newUser)
        .token("oldAccessToken")
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();

    var newAccessToken = JwtToken.builder()
        .user(newUser)
        .token("newAccessToken")
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();

    when(jwtService.generateToken(newUser)).thenReturn("newAccessToken");
    when(jwtService.generateRefreshToken(newUser)).thenReturn("newRefreshToken");
    when(tokenRepository.save(newAccessToken)).thenReturn(newAccessToken);
    when(tokenRepository.findAllValidTokensByUserId(0L)).thenReturn(List.of(oldAccessToken));

    Map<String, Object> response = authenticationService.login(request);

    assertTrue(response.containsKey("accessToken"));
    assertTrue(response.containsKey("refreshToken"));
    assertEquals("newAccessToken", response.get("accessToken"));
    assertEquals("newRefreshToken", response.get("refreshToken"));

    verify(authenticationManager, times(1)).authenticate(new UsernamePasswordAuthenticationToken(
        request.getEmail(), request.getPassword())
    );
    verify(userRepository, times(1)).findUserByEmail(request.getEmail());
    verify(jwtService, times(1)).generateToken(newUser);
    verify(jwtService, times(1)).generateRefreshToken(newUser);
    verify(tokenRepository, times(1)).findAllValidTokensByUserId(0L);
    verify(tokenRepository, times(1)).save(newAccessToken);
    verify(tokenRepository, times(1)).saveAll(List.of(oldAccessToken));

    log.info("Passed - Login service happy flow");
  }

  @Test
  public void testLoginUsernameNotFound() {
    log.info("Test running - Login service with non-existence username...");

    newUser.setEnabled(true);
    when(userRepository.findUserByEmail(newUser.getEmail())).thenThrow(NoSuchElementException.class);

    LoginDto request = LoginDto.builder()
        .email("newUser@email.com")
        .password("testPassword")
        .build();

    BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
        () -> authenticationService.login(request));
    assertEquals("Unable to login", exception.getMessage());
    Map<String, String> violations = new HashMap<>();
    violations.put("authentication", "Unable to authenticate with provided email and password. " +
        "Please check your inputs or if you have confirmed your account");
    assertEquals(violations, exception.getViolations());

    verify(authenticationManager, times(1)).authenticate(new UsernamePasswordAuthenticationToken(
        request.getEmail(), request.getPassword())
    );
    verify(userRepository, times(1)).findUserByEmail(request.getEmail());
    verify(jwtService, never()).generateToken(any());
    verify(jwtService, never()).generateRefreshToken(any());
    verify(tokenRepository, never()).findAllValidTokensByUserId(anyLong());

    log.info("Passed - Login service with non-existence username");
  }

  @Test
  public void testLoginWrongPassword() {
    log.info("Test running - Login service with wrong password...");

    newUser.setEnabled(true);

    LoginDto request = LoginDto.builder()
        .email("newUser@email.com")
        .password("testWrongPassword")
        .build();

    Map<String, String> violations = new HashMap<>();
    violations.put("authentication", "Unable to authenticate with provided email and password. " +
        "Please check your inputs or if you have confirmed your account");

    when(authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    )).thenThrow(new BadRequestDetailsException("Unable to login", violations));

    BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
        () -> authenticationService.login(request));
    assertEquals("Unable to login", exception.getMessage());
    assertEquals(violations, exception.getViolations());

    verify(authenticationManager, times(1)).authenticate(new UsernamePasswordAuthenticationToken(
        request.getEmail(), request.getPassword())
    );
    verify(userRepository, never()).findUserByEmail(request.getEmail());
    verify(jwtService, never()).generateToken(any());
    verify(jwtService, never()).generateRefreshToken(any());
    verify(tokenRepository, never()).findAllValidTokensByUserId(anyLong());

    log.info("Passed - Login service with wrong password");
  }

  @Test
  public void testLoginNonExistenceEmail() {
    log.info("Test running - Login service with non-existence email...");

    newUser.setEnabled(true);

    LoginDto request = LoginDto.builder()
        .email("nonUser@email.com")
        .password("testPassword")
        .build();

    Map<String, String> violations = new HashMap<>();
    violations.put("authentication", "Unable to authenticate with provided email and password. " +
        "Please check your inputs or if you have confirmed your account");

    when(userRepository.findUserByEmail(request.getEmail())).thenThrow(
        new BadRequestDetailsException("Unable to login", violations)
    );

    BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
        () -> authenticationService.login(request));
    assertEquals("Unable to login", exception.getMessage());
    assertEquals(violations, exception.getViolations());

    verify(authenticationManager, times(1)).authenticate(new UsernamePasswordAuthenticationToken(
        request.getEmail(), request.getPassword())
    );
    verify(userRepository, times(1)).findUserByEmail(request.getEmail());
    verify(jwtService, never()).generateToken(any());
    verify(jwtService, never()).generateRefreshToken(any());
    verify(tokenRepository, never()).findAllValidTokensByUserId(anyLong());

    log.info("Passed - Login service with non-existence email");
  }

  @Test
  public void testRefreshTokenHappyFlow() {
    log.info("Test running - Refresh access token happy flow...");

    newUser.setEnabled(true);
    newUser.setId(0L);
    String mockAuthenticationHeader = "Bearer refreshToken";

    var oldAccessToken = JwtToken.builder()
        .user(newUser)
        .token("oldAccessToken")
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();

    var newAccessToken = JwtToken.builder()
        .user(newUser)
        .token("newAccessToken")
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();

    when(request.getHeader("Authorization")).thenReturn(mockAuthenticationHeader);
    when(jwtService.extractUsername("refreshToken")).thenReturn(newUser.getEmail());
    when(userRepository.findUserByEmail(newUser.getEmail())).thenReturn(Optional.of(newUser));
    when(jwtService.isTokenValid("refreshToken", newUser)).thenReturn(true);
    when(jwtService.generateToken(newUser)).thenReturn(newAccessToken.getToken());
    when(tokenRepository.findAllValidTokensByUserId(0L)).thenReturn(List.of(oldAccessToken));
    when(tokenRepository.save(newAccessToken)).thenReturn(newAccessToken);

    Map<String, Object> responseBody = authenticationService.refreshToken(request, response);
    assertEquals("newAccessToken", responseBody.get("accessToken"));
    assertEquals("refreshToken", responseBody.get("refreshToken"));
    assertTrue(oldAccessToken.isExpired());
    assertTrue(oldAccessToken.isRevoked());
    assertFalse(newAccessToken.isExpired());
    assertFalse(newAccessToken.isRevoked());

    verify(request, times(1)).getHeader("Authorization");
    verify(jwtService, times(1)).extractUsername("refreshToken");
    verify(userRepository, times(1)).findUserByEmail(newUser.getEmail());
    verify(jwtService, times(1)).isTokenValid("refreshToken", newUser);
    verify(jwtService, times(1)).generateToken(newUser);
    verify(tokenRepository, times(1)).findAllValidTokensByUserId(0L);
    verify(tokenRepository, times(1)).save(newAccessToken);

    log.info("Passed - Refresh access token happy flow");
  }

  @Test
  public void testRefreshTokenAuthenticationHeaderInvalidFlow() {
    log.info("Test running - Refresh access token when authentication header is invalid...");

    newUser.setEnabled(true);
    newUser.setId(0L);
    String mockAuthenticationHeader = "NotBearer refreshToken";

    when(request.getHeader("Authorization")).thenReturn(mockAuthenticationHeader);

    BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
        () -> authenticationService.refreshToken(request, response));
    assertEquals("Unable to refresh token", exception.getMessage());
    Map<String, String> violations = new HashMap<>();
    violations.put("authentication", "Invalid header format for refreshing JWT");
    assertEquals(violations, exception.getViolations());

    verify(request, times(1)).getHeader("Authorization");
    verify(jwtService, never()).extractUsername(anyString());
    verify(userRepository, never()).findUserByEmail(anyString());
    verify(jwtService, never()).isTokenValid(anyString(), any());
    verify(jwtService, never()).generateToken(any());
    verify(tokenRepository, never()).findAllValidTokensByUserId(anyLong());
    verify(tokenRepository, never()).save(any());

    log.info("Passed - Refresh access token when authentication header is invalid");
  }

  @Test
  public void testRefreshTokenUserNotFoundFlow() {
    log.info("Test running - Refresh access token when user is not found...");

    newUser.setEnabled(true);
    newUser.setId(0L);
    String mockAuthenticationHeader = "Bearer refreshToken";

    when(request.getHeader("Authorization")).thenReturn(mockAuthenticationHeader);
    when(jwtService.extractUsername("refreshToken")).thenReturn(newUser.getEmail());
    when(userRepository.findUserByEmail(newUser.getEmail())).thenReturn(Optional.empty());

    BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
        () -> authenticationService.refreshToken(request, response));
    assertEquals("Unable to refresh token", exception.getMessage());
    Map<String, String> violations = new HashMap<>();
    violations.put("authentication", "Invalid header format for refreshing JWT");
    assertEquals(violations, exception.getViolations());

    verify(request, times(1)).getHeader("Authorization");
    verify(jwtService, times(1)).extractUsername("refreshToken");
    verify(userRepository, times(1)).findUserByEmail(newUser.getEmail());
    verify(jwtService, never()).isTokenValid(anyString(), any());
    verify(jwtService, never()).generateToken(any());
    verify(tokenRepository, never()).findAllValidTokensByUserId(anyLong());
    verify(tokenRepository, never()).save(any());

    log.info("Passed - Refresh access token when user is not found");
  }

  @Test
  public void testRefreshTokenInvalidTokenFlow() {
    log.info("Test running - Refresh access token when token is invalid...");

    newUser.setEnabled(true);
    newUser.setId(0L);
    String mockAuthenticationHeader = "Bearer refreshToken";

    when(request.getHeader("Authorization")).thenReturn(mockAuthenticationHeader);
    when(jwtService.extractUsername("refreshToken")).thenReturn(newUser.getEmail());
    when(userRepository.findUserByEmail(newUser.getEmail())).thenReturn(Optional.of(newUser));
    when(jwtService.isTokenValid("refreshToken", newUser)).thenReturn(false);

    BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
        () -> authenticationService.refreshToken(request, response));
    assertEquals("Unable to refresh token", exception.getMessage());
    Map<String, String> violations = new HashMap<>();
    violations.put("token", "Token is invalid");
    assertEquals(violations, exception.getViolations());

    verify(request, times(1)).getHeader("Authorization");
    verify(jwtService, times(1)).extractUsername("refreshToken");
    verify(userRepository, times(1)).findUserByEmail(newUser.getEmail());
    verify(jwtService, times(1)).isTokenValid("refreshToken", newUser);
    verify(jwtService, never()).generateToken(any());
    verify(tokenRepository, never()).findAllValidTokensByUserId(anyLong());
    verify(tokenRepository, never()).save(any());

    log.info("Passed - Refresh access token when token is invalid");
  }

  @Test
  public void testLogOutHappyFlow() {
    log.info("Test running - Log out happy flow...");

    newUser.setEnabled(true);
    newUser.setId(0L);

    String mockAuthenticationHeader = "Bearer accessToken";

    var accessToken = JwtToken.builder()
        .user(newUser)
        .token("accessToken")
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();

    when(request.getHeader("Authorization")).thenReturn(mockAuthenticationHeader);
    when(tokenRepository.findTokenByString("accessToken")).thenReturn(Optional.of(accessToken));

    try (
        MockedStatic<SecurityContextHolder> ccg = mockStatic(SecurityContextHolder.class)
    ) {
      authenticationService.logout(request, response);
      assertTrue(accessToken.isExpired());
      assertTrue(accessToken.isRevoked());

      verify(request, times(1)).getHeader("Authorization");
      verify(tokenRepository, times(1)).findTokenByString("accessToken");
      verify(tokenRepository, times(1)).save(accessToken);
      ccg.verify(SecurityContextHolder::clearContext, times(1));
    }

    log.info("Passed - Log out happy flow");
  }

  @Test
  public void testLogOutAuthenticationHeaderInvalidFlow() {
    log.info("Test running - Log out when authentication header is invalid...");

    newUser.setEnabled(true);
    newUser.setId(0L);

    String mockAuthenticationHeader = "NotBearer AccessToken";

    var accessToken = JwtToken.builder()
        .user(newUser)
        .token("accessToken")
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();

    when(request.getHeader("Authorization")).thenReturn(mockAuthenticationHeader);

    try (
        MockedStatic<SecurityContextHolder> ccg = mockStatic(SecurityContextHolder.class)
    ) {
      authenticationService.logout(request, response);
      assertFalse(accessToken.isExpired());
      assertFalse(accessToken.isRevoked());

      verify(request, times(1)).getHeader("Authorization");
      verify(tokenRepository, never()).findTokenByString("accessToken");
      verify(tokenRepository, never()).save(accessToken);
      ccg.verify(SecurityContextHolder::clearContext, never());
    }

    log.info("Passed - Log out when authentication header is invalid");
  }

  @Test
  public void testConfirmRegistrationHappyFlow() {
    log.info("Test running - Confirm registration happy flow...");

    newUser.setId(0L);
    assertFalse(newUser.isEnabled());

    when(userRepository.findUserByEmail(newUser.getEmail())).thenReturn(Optional.of(newUser));

    authenticationService.confirmRegistration(newUser.getEmail(), newUser.getConfirmationCode());
    assertTrue(newUser.isEnabled());

    verify(userRepository, times(1)).findUserByEmail(newUser.getEmail());
    verify(userRepository, times(1)).save(newUser);

    log.info("Passed - Confirm registration happy flow");
  }

  @Test
  public void testConfirmRegistrationUserNotFoundFlow() {
    log.info("Test running - Confirm registration when user is not found...");

    assertFalse(newUser.isEnabled());

    when(userRepository.findUserByEmail(newUser.getEmail())).thenReturn(Optional.empty());

    BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
        () -> authenticationService.confirmRegistration(newUser.getEmail(), newUser.getConfirmationCode())
    );
    assertEquals("Unable to confirm registration", exception.getMessage());
    Map<String, String> violations = new HashMap<>();
    violations.put("url", "Invalid confirmation link");
    assertEquals(violations, exception.getViolations());
    assertFalse(newUser.isEnabled());

    verify(userRepository, times(1)).findUserByEmail(newUser.getEmail());
    verify(userRepository, never()).save(newUser);

    log.info("Passed - Confirm registration when user is not found");
  }

  @Test
  public void testConfirmRegistrationCodeInvalid() {
    log.info("Test running - Confirm registration when confirmation code is invalid...");

    newUser.setId(0L);
    assertFalse(newUser.isEnabled());

    when(userRepository.findUserByEmail(newUser.getEmail())).thenReturn(Optional.of(newUser));

    BadRequestDetailsException exception = assertThrows(BadRequestDetailsException.class,
        () -> authenticationService.confirmRegistration(newUser.getEmail(), "falseMockConfirmationCode")
    );
    assertEquals("Unable to confirm registration", exception.getMessage());
    Map<String, String> violations = new HashMap<>();
    violations.put("url", "The code is invalid or already confirmed");
    assertEquals(violations, exception.getViolations());
    assertFalse(newUser.isEnabled());

    verify(userRepository, times(1)).findUserByEmail(newUser.getEmail());
    verify(userRepository, never()).save(newUser);

    log.info("Passed - Confirm registration when confirmation code is invalid");
  }
}
