package prgrms.neoike.controller.mapper;

import prgrms.neoike.domain.member.Address;
import prgrms.neoike.service.dto.memberdto.MemberDto;
import prgrms.neoike.controller.dto.MemberRequest;

public class MemberMapper {

    public static MemberDto mapMemberDto(MemberRequest memberRequest) {
        return MemberDto.builder()
                .name(memberRequest.name())
                .password(memberRequest.password())
                .phoneNumber(memberRequest.phoneNumber())
                .birthday(memberRequest.birthday())
                .email(memberRequest.email())
                .address(new Address(memberRequest.city(), memberRequest.street(), memberRequest.zipcode()))
                .gender(memberRequest.gender())
                .build();
    }

}
