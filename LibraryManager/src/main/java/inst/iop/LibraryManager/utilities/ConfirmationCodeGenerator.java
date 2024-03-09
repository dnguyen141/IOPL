package inst.iop.LibraryManager.utilities;

import java.security.SecureRandom;

public class ConfirmationCodeGenerator {

  private static final int UUID_LENGTH = 32;

  public static String generateSecuredUuid() {
    SecureRandom secureRandom = new SecureRandom();
    byte[] randomBytes = new byte[UUID_LENGTH / 2];

    secureRandom.nextBytes(randomBytes);

    StringBuilder sb = new StringBuilder(UUID_LENGTH);
    for (byte b : randomBytes) {
      sb.append(String.format("%02x", b));
    }

    return sb.toString();
  }
}
