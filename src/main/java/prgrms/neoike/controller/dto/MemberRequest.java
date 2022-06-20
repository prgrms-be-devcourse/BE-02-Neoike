package prgrms.neoike.controller.dto;

import prgrms.neoike.domain.member.CountryType;
import prgrms.neoike.domain.member.Gender;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record MemberRequest(
        @NotNull
        String email,

        @NotNull
        String password,

        @NotNull
        String name,

        @NotNull
        LocalDateTime birthday,

        @NotNull
        String city,

        @NotNull
        String street,

        @NotNull
        String zipcode,

        @NotNull
        CountryType countryCode,

        @NotNull
        String phoneNumber,

        @NotNull
        Gender gender
) {
}
