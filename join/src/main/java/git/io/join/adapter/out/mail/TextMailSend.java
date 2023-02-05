package git.io.join.adapter.out.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

public interface TextMailSend {
    void send(Naver.SenderRequest request);

    static Naver.SenderRequest request(String to, String text) {
        return new Naver.SenderRequest(to, "junnyland token", "junny-token : "  + text);
    }
    @Component
    @RequiredArgsConstructor
    class Naver implements TextMailSend {
        private final JavaMailSender javaMailSender;


        @Override
        public void send(SenderRequest request) {
            SimpleMailMessage mimeMessage = new SimpleMailMessage(){{
                setFrom("chbe5082@naver.com");
                setTo(request.to);
                setSubject(request.title);
                setText(request.text);
            }};
            javaMailSender.send(mimeMessage);
        }

        public record SenderRequest(String to, String title, String text){}
    }
}
