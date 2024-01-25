package inst.iop.LibraryManager.authentication.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import inst.iop.LibraryManager.authentication.entities.JwtToken;
import inst.iop.LibraryManager.authentication.entities.enums.Role;
import inst.iop.LibraryManager.authentication.entities.enums.TokenType;
import inst.iop.LibraryManager.authentication.exceptions.BadRequestDetailsException;
import inst.iop.LibraryManager.authentication.models.requests.LoginRequest;
import inst.iop.LibraryManager.authentication.models.responses.AuthenticationResponse;
import inst.iop.LibraryManager.authentication.models.requests.RegisterRequest;
import inst.iop.LibraryManager.authentication.entities.User;
import inst.iop.LibraryManager.authentication.repositories.TokenRepository;
import inst.iop.LibraryManager.authentication.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import static inst.iop.LibraryManager.authentication.utility.UserValidators.*;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  private final TokenRepository tokenRepository;

  private final PasswordEncoder passwordEncoder;

  private final JwtService jwtService;

  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest request) throws BadRequestDetailsException {
    Map<String, Object> violations = validateUserRequest(request, userRepository, true);
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
      String accessToken = jwtService.generateToken(user);
      String refreshToken = jwtService.generateRefreshToken(user);
      saveToken(user, accessToken);
      return new AuthenticationResponse(accessToken, refreshToken);
    }
    throw new BadRequestDetailsException(violations);
  }

  public AuthenticationResponse login(LoginRequest request) throws AuthenticationException {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );
    User user = userRepository.findUserByEmail(request.getEmail()).orElseThrow();
    String accessToken = jwtService.generateToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);
    revokeAllTokens(user);
    saveToken(user, accessToken);
    return new AuthenticationResponse(accessToken, refreshToken);
  }

  public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String authenticationHeader = request.getHeader("Authorization");
    if (authenticationHeader == null || !authenticationHeader.startsWith("Bearer ")) {
      return;
    }

    String refreshToken = authenticationHeader.substring(7);
    String email = jwtService.extractUsername(refreshToken);
    if (email != null) {
      User user = userRepository.findUserByEmail(email).orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllTokens(user);
        saveToken(user, accessToken);
        var authenticationResponse = new AuthenticationResponse(accessToken, refreshToken);
        new ObjectMapper().writeValue(response.getOutputStream(), authenticationResponse);
      }
    }
  }

  public void logout(HttpServletRequest request, HttpServletResponse response) {
    String authenticationHeader = request.getHeader("Authorization");
    if (authenticationHeader == null || !authenticationHeader.startsWith("Bearer ")) {
      return;
    }

    String token = authenticationHeader.substring(7);
    var storedToken = tokenRepository.findTokenByToken(token).orElse(null);
    if (storedToken != null) {
      storedToken.setExpired(true);
      storedToken.setRevoked(true);
      tokenRepository.save(storedToken);
      SecurityContextHolder.clearContext();
    }
  }

  private void saveToken(User user, String jwtToken) {
    JwtToken token = JwtToken.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
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
