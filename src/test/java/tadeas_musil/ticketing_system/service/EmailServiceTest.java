package tadeas_musil.ticketing_system.service;

import static org.assertj.core.api.Assertions.assertThat;

import javax.mail.internet.MimeMessage;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = { "spring.mail.host=localhost", 
                                "spring.mail.port=3025",
                                "spring.mail.username=username",
                                "spring.mail.password=password" })
public class EmailServiceTest {

  @Autowired
  private EmailService emailService;

  public GreenMail greenMail = new GreenMail(ServerSetupTest.SMTP);

  @BeforeEach
  public void setUp() {
    greenMail.start();
    greenMail.setUser("test@localhost", "username", "password");
  }

  @AfterEach
  public void stopMailServer() {
    greenMail.stop();
  }

  @Test
  public void sendHtmlEmail_shouldSendEmailInHtmlFormat() throws Exception {
    emailService.sendHtmlEmail("subject", "test@localhost", "Email text");
    MimeMessage[] messages = greenMail.getReceivedMessages();

    assertThat(messages).hasSize(1);
    assertThat(GreenMailUtil.getBody(messages[0])).isEqualTo("Email text");
    assertThat(messages[0].getSubject()).isEqualTo("subject");
    assertThat(messages[0].getContentType()).startsWith("text/html");

  }

}