package prgrms.neoike.service.converter;

import prgrms.neoike.service.dto.memberdto.MemberDto;
import prgrms.neoike.domain.member.*;
import prgrms.neoike.service.dto.memberdto.MemberResponse;

public class MemberMapper {

    public static Member mapMember(MemberDto memberDto) {
        return Member.builder()
                .name(memberDto.name())
                .password(new Password(memberDto.password()))
                .phoneNumber(new PhoneNumber(memberDto.countryCode(), memberDto.phoneNumber()))
                .birthDay(memberDto.birthday())
                .email(new Email(memberDto.email()))
                .address(memberDto.address())
                .gender(memberDto.gender())
                .build();
    }

    public static MemberResponse mapMemberResponse(Long memberId, String email) {
        return MemberResponse.builder()
                .memberId(memberId)
                .email(email)
                .build();
    }
}
