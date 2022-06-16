package prgrms.neoike.domain.member;

import javax.persistence.Embeddable;

@Embeddable
public class PhoneNumber {

    private int countryCode;

    private String phoneNumber;
}