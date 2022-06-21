package prgrms.neoike.domain.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordTest {

    @ParameterizedTest
    @ValueSource(strings = {"12345678", "12345678abc", "0123456789101112", "12345678!!!", "12345678!!!abc"})
    @DisplayName("유효하지 않은 패스워드 등록시 예외가 발생한다 - 패스워드규칙 : 8~16 글자 + 대소문자 + 특수문자 + 숫자")
    void passwordValidationTest(String password) {
        assertThrows(IllegalArgumentException.class, () -> new Password(password));
    }

    @Test
    @DisplayName("유효한 패스워드 등록시 정상적으로 패스워드가 생성된다")
    void createPasswordTest() {
        String password = "123abcAB!!";
        String encodedPassword  = new Password(password).getPassword();

        assertTrue(new BCryptPasswordEncoder().matches("123abcAB!!", encodedPassword));

    }
}