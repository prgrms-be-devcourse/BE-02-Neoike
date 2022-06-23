package prgrms.neoike.controller.dto.memberdto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        @JsonFormat(shape =JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
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
