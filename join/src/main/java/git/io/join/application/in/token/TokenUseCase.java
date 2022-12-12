package git.io.join.application.in.token;

import git.io.join.adapter.out.token.CacheRepository;
import git.io.join.adapter.out.mail.TextMailSend;
import git.io.join.adapter.out.user.entity.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static git.io.join.adapter.out.mail.TextMailSend.request;

public interface TokenUseCase {
    String makeToken(Token.EmailRequest emailRequest);

    @Service
    @RequiredArgsConstructor
    class Token implements TokenUseCase {
        private final CacheRepository cacheRepository;
        private final TextMailSend textMailSend;
        private final UserRepository userRepository;

        @Override
        public String makeToken(EmailRequest emailRequest) {
            if(cacheRepository.isExists(emailRequest.email)){
                throw new RuntimeException("이미 인증키가 발급되었습니다. 3분 후 다시 요청해 주세요.");
            }
            cacheRepository.save(emailRequest.email);

            String uuid = UUID.randomUUID().toString();

            textMailSend.send(request(emailRequest.email, uuid));
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
