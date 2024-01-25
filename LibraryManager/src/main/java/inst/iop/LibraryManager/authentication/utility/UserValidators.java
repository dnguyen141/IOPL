package inst.iop.LibraryManager.authentication.utility;

import inst.iop.LibraryManager.authentication.models.requests.AuthenticationRequestInterface;
import inst.iop.LibraryManager.authentication.repositories.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class UserValidators {
  private static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
  private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z]).{6,20}$";
  private static final String NAME_REGEX = "^(?=.*[a-zA-Z]).{2,20}$";

  public static int compareRole(String callerRole, String targetRole) {
    Map<String, Integer> hierarchy = new HashMap<>();
    hierarchy.put("ROLE_USER", 0);
    hierarchy.put("ROLE_MODERATOR", 1);
    hierarchy.put("ROLE_ADMIN", 2);

    int callerOrder = hierarchy.get(callerRole);
    int targetOrder = hierarchy.get(targetRole);
    return Integer.compare(callerOrder, targetOrder);
  }

  public static Map<String, Object> validateUserRequest(
      AuthenticationRequestInterface request, UserRepository userRepository, boolean isRegisterRequest) {
    Map<String, Object> violations = new HashMap<>();
    String email = request.getEmail();
    String password = request.getPassword();
    String confirmedPassword = request.getConfirmedPassword();
    String firstName = request.getFirstName();
    String lastName = request.getLastName();
    String role = request.getRole();

    boolean isEmailNull = email == null;
    boolean isPasswordNull = password == null;
    boolean isConfirmedPasswordNull = confirmedPassword == null;
    boolean isFirstNameNull = firstName == null;
    boolean isLastNameNull = lastName == null;
    boolean isRoleNull = role == null;

    if (isRegisterRequest && isEmailNull || !isEmailNull && !validateEmail(email) ||
        isRegisterRequest && checkIfUserExists(email, userRepository)) {
      violations.put("email", "The email address is either invalid or used");
    }

    if (isRegisterRequest && isPasswordNull || !isPasswordNull && !validatePassword(password)) {
      violations.put("password", "The password must contain at least one lowercase Latin character and " +
          "a length of at least 6 characters and a maximum of 20 characters");
    }

    if (isRegisterRequest && isPasswordNull && isConfirmedPasswordNull
        || !validateConfirmedPassword(password, confirmedPassword)) {
      violations.put("confirmedPassword", "The confirmed password must be the same to the password");
    }

    if (isRegisterRequest && isFirstNameNull || !isFirstNameNull && !validateFirstName(firstName)) {
      violations.put("firstName", "First name must not be blank and contain at least 2 characters and a maximum of " +
          "20 characters");
    }

    if (isRegisterRequest && isLastNameNull || !isLastNameNull && !validateLastName(lastName)) {
      violations.put("lastName", "Last name must not be blank and contain at least 2 characters and a maximum of " +
          "20 characters");
    }

    if (isRegisterRequest && isRoleNull || !isRoleNull && !validateRole(role)) {
      violations.put("role", "Role is invalid");
    }

    return violations;
  }

  public static boolean validateEmail(String email) {
    return patternMatcher(email, EMAIL_REGEX);
  }

  public static boolean checkIfUserExists(String email, UserRepository userRepository) {
    return userRepository.findUserByEmail(email).isPresent();
  }

  public static boolean validatePassword(String password) {
    return patternMatcher(password, PASSWORD_REGEX);
  }

  public static boolean validateConfirmedPassword(String password, String confirmedPassword) {
    if (password == null && confirmedPassword == null) {
      return true;
    }
    if (password == null || confirmedPassword == null) {
      return false;
    }
    return password.equals(confirmedPassword);
  }

  public static boolean validateFirstName(String firstName) {
    return patternMatcher(firstName, NAME_REGEX);
  }

  public static boolean validateLastName(String lastName) {
    return patternMatcher(lastName, NAME_REGEX);
  }

  public static boolean validateRole(String role) {
    return role.equals("USER") || role.equals("MODERATOR") || role.equals("ADMIN");
  }

  private static boolean patternMatcher(String text, String regex) {
    return Pattern.compile(regex).matcher(text).matches();
  }
}
