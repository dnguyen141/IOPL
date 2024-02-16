package inst.iop.LibraryManager.utilities.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import inst.iop.LibraryManager.authentication.repositories.TokenRepository;
import inst.iop.LibraryManager.authentication.services.JwtService;
import inst.iop.LibraryManager.utilities.responses.InformationApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  private final UserDetailsService userDetailsService;

  private final TokenRepository tokenRepository;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {

    try {
      String authenticationHeader = request.getHeader("Authorization");
      if (authenticationHeader == null || !authenticationHeader.startsWith("Bearer ")) {
        boolean checkIfUriMatches = Stream.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/confirm",
            "/api/v1/books/list",
            "/api/v1/books/search"
        ).anyMatch(uri -> request.getRequestURI().startsWith(uri));

        if (!checkIfUriMatches) {
          handleErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
              "Valid JWT is mandatory for this request");
          return;
        }

        filterChain.doFilter(request, response);
        return;
      }

      String token = authenticationHeader.substring(7);
      String email;
      try {
        email = jwtService.extractUsername(token);
      } catch (ExpiredJwtException | MalformedJwtException e) {
        handleErrorResponse(response, HttpServletResponse.SC_FORBIDDEN,
            "JWT is either invalid or malformed");
        return;
      }

      if (SecurityContextHolder.getContext().getAuthentication() != null) {
        filterChain.doFilter(request, response);
        return;
      }

      UserDetails userDetails;
      try {
        userDetails = userDetailsService.loadUserByUsername(email);
      } catch (UsernameNotFoundException e) {
        handleErrorResponse(response, HttpServletResponse.SC_FORBIDDEN,
            "Extracted username from JWT not found");
        return;
      }

      boolean jwtRepositoryCheck = tokenRepository
          .findTokenByString(token)
          .map(t -> !t.isExpired() && !t.isRevoked())
          .orElse(false);
      if (jwtService.isTokenValid(token, userDetails) && jwtRepositoryCheck) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      } else {
        handleErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
            "JWT is either invalid, revoked or expired");
        return;
      }

      filterChain.doFilter(request, response);
    } catch (RuntimeException e) {
      handleErrorResponse(response, HttpServletResponse.SC_EXPECTATION_FAILED, e.getMessage());
    }
  }

  private void handleErrorResponse(HttpServletResponse response, int code, String message)
      throws IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(code);

    new ObjectMapper().writeValue(
        response.getOutputStream(),
        new InformationApiResponse(
            "error",
            code,
            message
        )
    );
  }
}
