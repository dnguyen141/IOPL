package inst.iop.LibraryManager.authentication.filters;

import inst.iop.LibraryManager.authentication.models.CustomHttpServletRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TrailingSlashFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {
    final String uri = request.getRequestURI();
    final String newUri;
    final HttpServletRequest alternateRequest;
    if (request.getRequestURI().endsWith("/") && request.getRequestURI().length() > 1) {
      newUri = uri.substring(0, uri.length() - 1);
      alternateRequest = new CustomHttpServletRequestWrapper(request, newUri);
      filterChain.doFilter(alternateRequest, response);
    }
    filterChain.doFilter(request, response);
  }
}
