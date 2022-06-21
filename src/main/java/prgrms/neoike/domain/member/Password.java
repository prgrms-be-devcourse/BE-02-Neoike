package prgrms.neoike.domain.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.util.regex.Pattern;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {

    private static final String PASSWORD_REGEX = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,16}$";

    private String password;

    public Password(String password) {
        validatePasswordPattern(password);
        this.password = password;
    }

    private void validatePasswordPattern(String password) {
        boolean matches = Pattern.matches(PASSWORD_REGEX, password);
        if (!matches) {
            throw new IllegalArgumentException("입력값이 패스워드 형식에 맞지 않습니다.");
        }
    }
}