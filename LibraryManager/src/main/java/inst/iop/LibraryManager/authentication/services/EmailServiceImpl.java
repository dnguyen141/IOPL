package inst.iop.LibraryManager.authentication.services;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailServiceImpl implements EmailService {

  @Value("${spring.sendgrid.api-key}")
  private String sendgridApi;

  @Override
  public boolean sendConfirmationEmailToRecipient(String to, String firstname, String lastName,
                                               String confirmationCode) throws IOException {
    Email fromEmail = new Email("minhdinhnguyen1495@gmail.com");
    String subject = "Please confirm your registration at IOPL";
    Email toEmail = new Email(to);
    Content content = new Content(
        "text/plain", constructConfirmationEmailContent(firstname, lastName, to, confirmationCode)
    );
    Mail mail = new Mail(fromEmail, subject, toEmail, content);

    SendGrid sg = new SendGrid(sendgridApi);
    Request request = new Request();
    request.setMethod(Method.POST);
    request.setEndpoint("mail/send");
    request.setBody(mail.build());

    Response response = sg.api(request);
    int statusCode = response.getStatusCode();
    return statusCode >= 200 && statusCode < 300;
  }

  private String constructConfirmationEmailContent(String firstname, String lastName, String email,
                                                   String confirmationCode) {
    return "Hi " + firstname + " " + lastName + ",\n" +
        "Welcome to IOP's library. Please confirm your registration by clicking on the following URL:\n" +
        "http://localhost:8080/api/v1/auth/confirm?u=" + email + "&c=" + confirmationCode + " \n\n" +
        "Sincerely,\nIOPL team";
  }
}
