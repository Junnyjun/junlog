package git.io.join.application.in.token;

import git.io.join.adapter.out.token.CacheRepository;
import git.io.join.adapter.out.user.entity.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static git.io.join.adapter.out.mail.TextMailSend.request;

public interface TokenUseCase {
    String makeToken(Token.EmailRequest emailRequest);

    @Service
    @RequiredArgsConstructor
    @Slf4j
    class Token implements TokenUseCase {
        private final CacheRepository cacheRepository;
        private final UserRepository userRepository;
        private final RabbitTemplate rabbitTemplate;

        @Value("${spring.rabbitmq.template.exchange}")
        private String exchange;
        @Value("${spring.rabbitmq.template.routing-key}")
        private String key;

        @Override
        public String makeToken(EmailRequest emailRequest) {
            if (cacheRepository.size() > 5) {
                throw new RuntimeException("인증키 요청이 너무 많습니다. 잠시 후 다시 요청해 주세요.");
            }

            if (cacheRepository.isExists(emailRequest.email)) {
                Long timer = userRepository.timer(emailRequest.email);
                throw new RuntimeException("이미 인증키가 발급되었습니다." + timer + "초 후 다시 요청해 주세요.");
            }

            cacheRepository.save(emailRequest.email);
            final String uuid = UUID.randomUUID().toString();

            rabbitTemplate.convertAndSend(exchange, key, request(emailRequest.email, uuid));
            log.info("request mq {}", emailRequest.email);
//            textMailSend.send(request(emailRequest.email, uuid));
            userRepository.init(emailRequest.email, uuid);

            return emailRequest.email;
        }

        public record EmailRequest(String email) {
            public EmailRequest {
                if (email == null || email.isBlank()) {
                    throw new IllegalArgumentException("필수값 입니다");
                }
            }
        }

    }


}
