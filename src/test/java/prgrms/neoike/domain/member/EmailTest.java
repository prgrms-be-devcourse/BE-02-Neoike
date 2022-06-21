package prgrms.neoike.domain.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @ParameterizedTest
    @DisplayName("이메일 형식에 맞지않으면 예외가 발생한다")
    @ValueSource(strings = {"user@gmail", "usergmail", "@gmail.com", "user@.com"})
    void emailValidationTest(String email) {
        assertThrows(IllegalArgumentException.class, () -> new Email(email));
    }

    @Test
    @DisplayName("이메일의 영문자는 소문자로 치환된다")
    void emailLowerCaseTest() {
        String upperCaseEmail = "TEST123@GMAIL.COM";
        Email email = new Email(upperCaseEmail);

        assertThat(email.getEmail()).isEqualTo("test123@gmail.com");
    }
}