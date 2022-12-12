package git.io.join;

import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(args = {"--auth=junnylandauthkey1234567891234512","--iv=0123456789abcdef"})
public interface IntegrationTestBase { }