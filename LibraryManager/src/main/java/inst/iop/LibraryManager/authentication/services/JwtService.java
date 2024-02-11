package inst.iop.LibraryManager.authentication.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Map;
import java.util.function.Function;

public interface JwtService {
  String generateToken(UserDetails userDetails);

  String generateRefreshToken(UserDetails userDetails);

  String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration);

  String extractUsername(String token);

  <T> T extractClaims(String token, Function<Claims, T> claimsResolver);

  boolean isTokenValid(String token, UserDetails userDetails);

  boolean isTokenForUser(String token, UserDetails userDetails);

  boolean isTokenExpired(String token);
}
