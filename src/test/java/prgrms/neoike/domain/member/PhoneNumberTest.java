package prgrms.neoike.domain.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PhoneNumberTest {

    @ParameterizedTest
    @ValueSource(strings = {"010-1234-1566", "01312341234", "01211112", "019++123455"})
    @DisplayName("유효하지 않은 핸드폰번호를 입력하면 예외가 발생한다")
    void phoneNumberValidationTest(String phoneNumber) {
        assertThrows(IllegalArgumentException.class, () -> new PhoneNumber(CountryType.KOR, phoneNumber));
    }

    @ParameterizedTest
    @ValueSource(strings = {"01012341566", "0161231234"})
    @DisplayName("유효한 핸드폰번호를 입력하면 핸드폰번호가 정상 생성된다")
    void createPhoneNumberTest(String phoneNumber) {
        PhoneNumber createdPhoneNumber = new PhoneNumber(CountryType.KOR, phoneNumber);
        assertThat(createdPhoneNumber.getPhoneNumber()).isEqualTo(phoneNumber);
    }

}