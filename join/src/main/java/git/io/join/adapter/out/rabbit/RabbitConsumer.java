package git.io.join.adapter.out.rabbit;


import git.io.join.adapter.out.mail.TextMailSend;
import git.io.join.adapter.out.mail.TextMailSend.Naver.SenderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitConsumer {

    private final TextMailSend textMailSend;

    @RabbitListener(queues = "mail.queue")
    public void consume(SenderRequest senderRequest) {
        textMailSend.send(senderRequest);
        log.info("send mail {}", senderRequest);
    }
}
