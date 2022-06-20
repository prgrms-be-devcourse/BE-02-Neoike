package prgrms.neoike.service.mapper;

import prgrms.neoike.controller.dto.MemberDto;
import prgrms.neoike.domain.member.*;

public class MemberMapper {

    public static Member mapMember(MemberDto memberDto) {
        return Member.builder()
                .name(memberDto.name())
                .password(new Password(memberDto.password()))
                .phoneNumber(new PhoneNumber(memberDto.countryCode(), memberDto.phoneNumber()))
                .birthDay(memberDto.birthday())
                .email(new Email(memberDto.email()))
                .address(new Address(memberDto.city(), memberDto.street(), memberDto.zipcode()))
                .gender(memberDto.gender())
                .build();
    }
}
