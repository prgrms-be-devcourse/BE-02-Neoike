package prgrms.neoike.domain.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.Embeddable;
import java.util.regex.Pattern;

import static java.text.MessageFormat.format;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {

    private static final String PASSWORD_REGEX = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,16}$";

    private String password;

    public Password(String password) {
        validatePasswordPattern(password);
        String encodePassword = encodePassword(password);
        this.password = encodePassword;
    }

    private void validatePasswordPattern(String password) {
        boolean matches = Pattern.matches(PASSWORD_REGEX, password);
        if (!matches) {
            throw new IllegalArgumentException(format("입력값이 패스워드 형식에 맞지 않습니다. inputPassword : {0}", password));
        }
    }

    private String encodePassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}