package prgrms.neoike.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import prgrms.neoike.domain.member.Address;
import prgrms.neoike.domain.member.Email;
import prgrms.neoike.domain.member.Gender;
import prgrms.neoike.domain.member.Member;
import prgrms.neoike.repository.MemberRepository;
import prgrms.neoike.service.dto.memberdto.MemberDto;
import prgrms.neoike.service.dto.memberdto.MemberResponse;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원가입을 한다")
    void joinTest() {
        //given
        MemberDto memberDto = addTestMemberDto();
        Member member = mock(Member.class);

        given(memberRepository.save(any())).willReturn(member);
        when(member.getId()).thenReturn(1L);
        when(member.getEmail()).thenReturn(new Email("test@gmail.com"));

        //when
        MemberResponse joinMemberResponse = memberService.join(memberDto);

        //then
        assertThat(joinMemberResponse.memberId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("기존에 존재하는 이메일로 회원가입을 하면, 오류가 발생한다")
    void duplicateEmailJoinTest() {
        //given
        MemberDto memberDto = addTestMemberDto();
        Member member = mock(Member.class);

        when(member.getEmail()).thenReturn(new Email("test@gmail.com"));
        given(memberRepository.findByEmail(member.getEmail().getEmail()))
                .willReturn(Optional.of(member));
        //when

        //then
        assertThrows(IllegalArgumentException.class, () -> memberService.join(memberDto));
    }

    private MemberDto addTestMemberDto() {
        return MemberDto.builder()
                .name("testUser")
                .password("testPassword123!")
                .phoneNumber("01023451234")
                .birthday(LocalDateTime.now())
                .email("test@gmail.com")
                .address(new Address("seoul", "samsungro", "12345"))
                .gender(Gender.FEMALE)
                .build();
    }
}