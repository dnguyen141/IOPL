package inst.iop.LibraryManager.authentication.services;

import java.io.IOException;

public interface EmailService {

  boolean sendConfirmationEmailToRecipient(String toEmail, String firstname, String lastName,
                                        String confirmationCode) throws IOException;
}
