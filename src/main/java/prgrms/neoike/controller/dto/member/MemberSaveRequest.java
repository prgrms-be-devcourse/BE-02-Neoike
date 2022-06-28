package prgrms.neoike.controller.dto.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import prgrms.neoike.domain.member.CountryType;
import prgrms.neoike.domain.member.Gender;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public record MemberSaveRequest(
        @NotBlank
        String email,

        @NotBlank
        String password,

        @NotBlank
        String name,

        @NotNull
        @JsonFormat(shape =JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate birthday,

        @NotBlank
        String city,

        @NotBlank
        String street,

        @NotBlank
        String zipcode,

        @NotNull
        CountryType countryCode,

        @NotBlank
        String phoneNumber,

        @NotNull
        Gender gender
) {
}
