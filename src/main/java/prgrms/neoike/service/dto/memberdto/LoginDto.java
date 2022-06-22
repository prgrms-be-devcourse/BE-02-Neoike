package prgrms.neoike.service.dto.memberdto;

import lombok.Builder;

@Builder
public record LoginDto(String email, String password) {
}
