package git.io.join.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Component
public class Encrypt {

    public static final String PADDING = "AES/CBC/PKCS5Padding";
    private final String iv;
    private final String key;

    public Encrypt(ApplicationArguments arguments) {
        this.iv = arguments.getOptionValues("iv") == null ? "" : arguments.getOptionValues("iv").get(0);
        this.key = arguments.getOptionValues("auth") == null ? "" : arguments.getOptionValues("auth").get(0);

        log.info("iv: {}", iv);
        log.info("key: {}", key);
    }

    public String encryptAES256(String text) {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());

        try {
            Cipher cipher = Cipher.getInstance(PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);
            byte[] encrypted = cipher.doFinal(text.getBytes(UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String decryptAES256(String cipherText){
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());

        try {
            Cipher cipher = Cipher.getInstance(PADDING);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);
            byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
            return new String(cipher.doFinal(decodedBytes), UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
