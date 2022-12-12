package git.io.join.adapter.in;

import git.io.join.adapter.MessageResponse;
import git.io.join.application.in.token.TokenUseCase;
import git.io.join.application.in.token.TokenUseCase.Token.EmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

public interface TokenController {

    @RestController
    @RequestMapping("/api/v1")
    @RequiredArgsConstructor
    class ToekenHttpIn implements TokenController{
        private final TokenUseCase tokenUseCase;

        @PostMapping("/token")
        @ResponseStatus(HttpStatus.CREATED)
        public ResponseEntity<MessageResponse> createToken(@RequestBody Request request) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MessageResponse(tokenUseCase.makeToken(request.toUsecase()) + " 메일함을 확인 해주세요"));
        }
        record Request(String email) {
            Request {
                if (!Pattern.matches("^[a-zA-Z0-9]+@(naver.com|gmail.com)$", email)) {
                    throw new IllegalArgumentException("naver.com&gmail.com 만 사용 가능 합니다.");
                }
            }

            private EmailRequest toUsecase(){return new EmailRequest(email);}
        }
    }

}
