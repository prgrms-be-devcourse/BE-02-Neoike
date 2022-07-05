package prgrms.neoike.domain.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.util.regex.Pattern;

import static java.text.MessageFormat.format;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhoneNumber {

    private static final String PHONE_NUMBER_REGEX = "^01([0|1|6|7|8|9])([0-9]{3,4})([0-9]{4})$";
    private CountryType countryType;
    private String phoneNumber;

    public PhoneNumber(CountryType countryType, String phoneNumber) {
        validatePhoneNumberPattern(phoneNumber);
        this.countryType = countryType;
        this.phoneNumber = phoneNumber;
    }

    private void validatePhoneNumberPattern(String phoneNumber) {
        boolean matches = Pattern.matches(PHONE_NUMBER_REGEX, phoneNumber);
        if (!matches) {
            throw new IllegalArgumentException(format("입력값이 핸드폰번호 형식에 맞지 않습니다. inputPhoneNumber : {0}", phoneNumber));
        }
    }

}