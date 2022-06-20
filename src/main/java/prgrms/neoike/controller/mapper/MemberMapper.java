package prgrms.neoike.controller.mapper;

import prgrms.neoike.controller.dto.MemberDto;
import prgrms.neoike.controller.dto.MemberRequest;

public class MemberMapper {

    public static MemberDto mapMemberDto(MemberRequest memberRequest) {
        return MemberDto.builder()
                .name(memberRequest.name())
                .password(memberRequest.password())
                .phoneNumber(memberRequest.phoneNumber())
                .birthday(memberRequest.birthday())
                .email(memberRequest.email())
                .city(memberRequest.city())
                .street(memberRequest.street())
                .zipcode(memberRequest.zipcode())
                .gender(memberRequest.gender())
                .build();
    }

}
