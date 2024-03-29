package inst.iop.LibraryManager.authentication.services;

import inst.iop.LibraryManager.authentication.dtos.LoginDto;
import inst.iop.LibraryManager.authentication.dtos.RegisterDto;
import inst.iop.LibraryManager.authentication.entities.JwtToken;
import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.authentication.entities.enums.Role;
import inst.iop.LibraryManager.authentication.entities.enums.TokenType;
import inst.iop.LibraryManager.authentication.repositories.TokenRepository;
import inst.iop.LibraryManager.authentication.repositories.UserRepository;
import inst.iop.LibraryManager.utilities.exceptions.BadRequestDetailsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static inst.iop.LibraryManager.utilities.ConfirmationCodeGenerator.generateSecuredUuid;
import static inst.iop.LibraryManager.utilities.ConstraintViolationSetHandler.convertSetToMap;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

  private final UserRepository userRepository;

  private final TokenRepository tokenRepository;

  private final PasswordEncoder passwordEncoder;

  private final JwtService jwtService;

  private final AuthenticationManager authenticationManager;

  private final EmailService emailService;

  private final Validator validator;

  @Override
  @Transactional
  public User register(RegisterDto request) throws BadRequestDetailsException {
    Map<String, String> violations = convertSetToMap(validator.validate(request));

    if (request.getPassword() == null || !request.getPassword().equals(request.getConfirmedPassword())) {
      violations.put("confirmedPassword", "Password and confirmed password must be matched");
    }

    if (!violations.isEmpty()) {
      throw new BadRequestDetailsException("Invalid register request", violations);
    }

    Optional<User> u = userRepository.findUserByEmail(request.getEmail());
    if (u.isPresent()) {
      violations.put("email", "An user with the same email is already existed");
      throw new BadRequestDetailsException("Invalid register request", violations);
    }

    String confirmationCode = generateSecuredUuid();
    boolean isSendingSuccess;
    try {
      isSendingSuccess = emailService.sendConfirmationEmailToRecipient(
          "minhdinhnguyen1495@gmail.com", request.getFirstName(), request.getLastName(), confirmationCode
      );
    } catch (IOException e) {
      isSendingSuccess = false;
    }

    if (!isSendingSuccess) {
      violations.put("email", "Unable to send confirmation email");
      throw new BadRequestDetailsException("Invalid register request", violations);
    }

    User user = User.builder()
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .role(Role.USER)
        .borrowEntries(new HashSet<>())
        .createdDate(LocalDate.now())
        .enabled(false)
        .confirmationCode(confirmationCode)
        .build();
    userRepository.save(user);
    return user;
  }

  @Override
  @Transactional
  public Map<String, Object> login(LoginDto request) throws BadRequestDetailsException {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
      );
      User user = userRepository.findUserByEmail(request.getEmail()).orElseThrow();
      String accessToken = jwtService.generateToken(user);
      String refreshToken = jwtService.generateRefreshToken(user);
      revokeAllTokens(user);
      saveToken(user, accessToken);
      Map<String, Object> details = new HashMap<>();
      details.put("accessToken", accessToken);
      details.put("refreshToken", refreshToken);
      return details;
    } catch (AuthenticationException | NoSuchElementException e) {
      Map<String, String> violations = new HashMap<>();
      violations.put("authentication", "Unable to authenticate with provided email and password. " +
          "Please check your inputs or if you have confirmed your account");
      throw new BadRequestDetailsException("Unable to login", violations);
    }
  }

  @Override
  @Transactional
  public Map<String, Object> refreshToken(HttpServletRequest request, HttpServletResponse response)
      throws BadRequestDetailsException {
    String authenticationHeader = request.getHeader("Authorization");
    if (authenticationHeader == null || !authenticationHeader.startsWith("Bearer ")) {
      Map<String, String> violations = new HashMap<>();
      violations.put("authentication", "Invalid header format for refreshing JWT");
      throw new BadRequestDetailsException("Unable to refresh token", violations);
    }

    String refreshToken = authenticationHeader.substring(7);

    String email = jwtService.extractUsername(refreshToken);
    User user = userRepository.findUserByEmail(email).orElseThrow(() -> {
      Map<String, String> violations = new HashMap<>();
      violations.put("authentication", "Invalid header format for refreshing JWT");
      return new BadRequestDetailsException("Unable to refresh token", violations);
    });

    if (jwtService.isTokenValid(refreshToken, user)) {
      var accessToken = jwtService.generateToken(user);
      revokeAllTokens(user);
      saveToken(user, accessToken);
      Map<String, Object> details = new HashMap<>();
      details.put("accessToken", accessToken);
      details.put("refreshToken", refreshToken);
      return details;
    }

    Map<String, String> violations = new HashMap<>();
    violations.put("token", "Token is invalid");
    throw new BadRequestDetailsException("Unable to refresh token", violations);
  }

  @Override
  @Transactional
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    String authenticationHeader = request.getHeader("Authorization");
    if (authenticationHeader == null || !authenticationHeader.startsWith("Bearer ")) {
      return;
    }

    String token = authenticationHeader.substring(7);
    Optional<JwtToken> storedToken = tokenRepository.findTokenByString(token);
    if (storedToken.isPresent()) {
      storedToken.get().setExpired(true);
      storedToken.get().setRevoked(true);
      tokenRepository.save(storedToken.get());
    }
    SecurityContextHolder.clearContext();
  }

  @Override
  @Transactional
  public void confirmRegistration(String email, String confirmationCode) throws BadRequestDetailsException {
    User user = userRepository.findUserByEmail(email).orElseThrow(() -> {
      Map<String, String> violation = new HashMap<>();
      violation.put("url", "Invalid confirmation link");
      return new BadRequestDetailsException("Unable to confirm registration", violation);
    });

    if (user.getConfirmationCode() != null && user.getConfirmationCode().equals(confirmationCode)
        && !user.isEnabled()) {
      user.setEnabled(true);
      user.setConfirmationCode(null);
      userRepository.save(user);
    } else {
      Map<String, String> violation = new HashMap<>();
      violation.put("url", "The code is invalid or already confirmed");
      throw new BadRequestDetailsException("Unable to confirm registration", violation);
    }
  }

  @Override
  public JwtToken saveToken(User user, String jwtToken) {
    var token = JwtToken.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
    return token;
  }

  private void revokeAllTokens(User user) {
    var storedTokens = tokenRepository.findAllValidTokensByUserId(user.getId());
    if (!storedTokens.isEmpty()) {
      storedTokens.forEach(t -> {
        t.setExpired(true);
        t.setRevoked(true);
      });
      tokenRepository.saveAll(storedTokens);
    }
  }
}
