package prgrms.neoike.controller.mapper;

import prgrms.neoike.controller.dto.memberdto.LoginRequest;
import prgrms.neoike.domain.member.Address;
import prgrms.neoike.service.dto.memberdto.LoginDto;
import prgrms.neoike.service.dto.memberdto.MemberDto;
import prgrms.neoike.controller.dto.memberdto.MemberRequest;

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

    public static LoginDto toLoginDto(LoginRequest loginRequest) {
        return LoginDto.builder()
                .email(loginRequest.email())
                .password(loginRequest.password())
                .build();
    }

}
