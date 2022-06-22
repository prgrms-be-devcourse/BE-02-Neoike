package prgrms.neoike.controller.dto.memberdto;

import javax.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank
        String email,

        @NotBlank
        String password
) {
}
