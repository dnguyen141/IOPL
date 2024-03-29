package inst.iop.LibraryManager.utilities.filters;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;


public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {
  private final String customURI;

  /**
   * Constructs a request object wrapping the given request.
   *
   * @param request The request to wrap
   * @throws IllegalArgumentException if the request is null
   */
  public CustomHttpServletRequestWrapper(HttpServletRequest request, String customURI) {
    super(request);
    this.customURI = customURI;
  }

  @Override
  public String getRequestURI() {
    return this.customURI;
  }

}
