package git.io.join.adapter.out.mail;

import git.io.join.config.Encrypt;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

public interface TextMailSend {
    void send(Naver.SenderRequest request);

    static Naver.SenderRequest toFormat(String to, String title, String text) {
        return new Naver.SenderRequest(to, title, text);
    }
    @Component
    class Naver implements TextMailSend {
        private final JavaMailSender javaMailSender;
        private final String from;

        public Naver(JavaMailSender javaMailSender,
                     Encrypt encrypt,
                     @Value("${mail.host}") String from) {
            this.javaMailSender = javaMailSender;
            this.from = encrypt.decryptAES256(from);
        }

        @Override
        public void send(SenderRequest request) {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mailHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
            try {
                mailHelper.setFrom(from);
                mailHelper.setTo(request.to);
                mailHelper.setSubject(request.title);
                mailHelper.setText(request.text);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            javaMailSender.send(mimeMessage);
        }

        private record SenderRequest(String to, String title, String text){}
    }
}
