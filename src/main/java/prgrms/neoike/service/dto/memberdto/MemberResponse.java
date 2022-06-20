package prgrms.neoike.service.dto.memberdto;

import lombok.Builder;

public record MemberResponse(Long memberId, String email) {

    @Builder
    public MemberResponse {

    }
}
