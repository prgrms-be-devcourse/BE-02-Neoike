package prgrms.neoike.service.dto.memberdto;

import lombok.Builder;
import prgrms.neoike.domain.member.Address;
import prgrms.neoike.domain.member.CountryType;
import prgrms.neoike.domain.member.Gender;

import java.time.LocalDateTime;

public record MemberDto(
        String email,
        String password,
        String name,
        LocalDateTime birthday,
        Address address,
        CountryType countryCode,
        String phoneNumber,
        Gender gender
) {
    @Builder
    public MemberDto{

    }
}
