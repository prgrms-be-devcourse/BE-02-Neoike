package prgrms.neoike.service.dto.member;

import lombok.Builder;
import prgrms.neoike.domain.member.Address;
import prgrms.neoike.domain.member.CountryType;
import prgrms.neoike.domain.member.Gender;

import java.time.LocalDate;

@Builder
public record MemberDto(
    String email,
    String password,
    String name,
    LocalDate birthday,
    Address address,
    CountryType countryCode,
    String phoneNumber,
    Gender gender
) {
}
