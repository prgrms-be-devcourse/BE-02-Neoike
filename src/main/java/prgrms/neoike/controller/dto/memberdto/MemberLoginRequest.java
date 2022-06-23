package prgrms.neoike.controller.dto.memberdto;

import javax.validation.constraints.NotBlank;

public record MemberLoginRequest(
        @NotBlank
        String email,

        @NotBlank
        String password
) {
}
