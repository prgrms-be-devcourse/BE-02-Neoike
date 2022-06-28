package prgrms.neoike.controller.mapper;

import prgrms.neoike.controller.dto.member.MemberSaveRequest;
import prgrms.neoike.domain.member.Address;
import prgrms.neoike.service.dto.member.MemberDto;

public class MemberMapper {

    public static MemberDto toMemberDto(MemberSaveRequest memberSaveRequest) {
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
}
