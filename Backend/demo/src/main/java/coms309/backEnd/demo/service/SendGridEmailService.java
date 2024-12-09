package coms309.backEnd.demo.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Content;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;



import java.io.IOException;

@Service
public class SendGridEmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    public String sendEmail(String toEmail, String subject, String body) {
        Email from = new Email("isupulse@gmail.com"); // Replace with your verified SendGrid email
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sendGrid = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);

            // Check the response status code
            if (response.getStatusCode() == 202) {
                return "Email sent successfully to " + toEmail;
            } else {
                return "Failed to send email: " + response.getBody();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return "Error while sending email: " + ex.getMessage();
        }
    }
}
