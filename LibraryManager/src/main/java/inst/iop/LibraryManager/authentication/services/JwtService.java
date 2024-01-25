package inst.iop.LibraryManager.authentication.services;

import inst.iop.LibraryManager.authentication.repositories.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static io.jsonwebtoken.Jwts.SIG.HS256;

@Service
@RequiredArgsConstructor
public class JwtService {

  @Value("${application.security.jwt.secret-key}")
  private String SECRET_KEY;

  @Value("${application.security.jwt.expiration}")
  private long JWT_EXPIRATION;

  @Value("${application.security.jwt.refresh-token.expiration}")
  private long REFRESH_JWT_EXPIRATION;

  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails, JWT_EXPIRATION);
  }

  public String generateRefreshToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails, REFRESH_JWT_EXPIRATION);
  }

  public String generateToken(
     Map<String, Object> extraClaims,
     UserDetails userDetails,
     long expiration
  ) {
    return Jwts.builder()
        .issuer("IOP")
        .claims(extraClaims)
        .subject(userDetails.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSignInKey(), HS256)
        .compact();
  }

  private SecretKey getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String extractUsername(String token) {
    return extractClaims(token, Claims::getSubject);
  }

  public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
    return claimsResolver.apply(extractAllClaims(token));
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSignInKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    return isTokenForUser(token, userDetails) && !isTokenExpired(token);
  }

  public boolean isTokenForUser(String token, UserDetails userDetails) {
    return userDetails.getUsername().equals(extractUsername(token));
  }

  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaims(token, Claims::getExpiration);
  }
}
