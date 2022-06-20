package prgrms.neoike.controller.dto;

import lombok.Builder;
import prgrms.neoike.domain.member.CountryType;
import prgrms.neoike.domain.member.Gender;

import java.time.LocalDateTime;

public record MemberDto(
        String email,
        String password,
        String name,
        LocalDateTime birthday,
        String city,
        String street,
        String zipcode,
        CountryType countryCode,
        String phoneNumber,
        Gender gender
) {
    @Builder
    public MemberDto{

    }
}
