package prgrms.neoike.controller.mapper;

import prgrms.neoike.controller.dto.memberdto.MemberLoginRequest;
import prgrms.neoike.domain.member.Address;
import prgrms.neoike.service.dto.memberdto.LoginDto;
import prgrms.neoike.service.dto.memberdto.MemberDto;
import prgrms.neoike.controller.dto.memberdto.MemberSaveRequest;

public class MemberMapper {

    public static MemberDto mapMemberDto(MemberSaveRequest memberSaveRequest) {
        return MemberDto.builder()
                .name(memberSaveRequest.name())
                .password(memberSaveRequest.password())
                .phoneNumber(memberSaveRequest.phoneNumber())
                .birthday(memberSaveRequest.birthday())
                .email(memberSaveRequest.email())
                .address(new Address(memberSaveRequest.city(), memberSaveRequest.street(), memberSaveRequest.zipcode()))
                .gender(memberSaveRequest.gender())
                .build();
    }

    public static LoginDto toLoginDto(MemberLoginRequest memberLoginRequest) {
        return new LoginDto(memberLoginRequest.email(), memberLoginRequest.password());
    }

}
