package inst.iop.LibraryManager.authentication.models.requests;


public interface AuthenticationRequestInterface {
  default String getEmail() {
    return null;
  }

  default String getPassword() {
    return null;
  }

  default String getConfirmedPassword() {
    return null;
  }

  default String getFirstName() {
    return null;
  }

  default String getLastName() {
    return null;
  }

  default String getRole() {
    return null;
  }
}
