package prgrms.neoike.domain.member;

import lombok.Getter;

@Getter
public enum CountryType {
    KOR("대한민국", 82);

    private String countryName;
    private int countryCode;

    CountryType(String countryName, int countryCode) {
        this.countryName = countryName;
        this.countryCode = countryCode;
    }
}
