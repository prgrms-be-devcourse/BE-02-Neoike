package prgrms.neoike.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import prgrms.neoike.domain.member.Address;
import prgrms.neoike.domain.member.Email;
import prgrms.neoike.domain.member.Gender;
import prgrms.neoike.domain.member.Member;
import prgrms.neoike.repository.MemberRepository;
import prgrms.neoike.service.dto.member.MemberDto;
import prgrms.neoike.service.dto.member.MemberResponse;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private DrawTicketService drawTicketService;

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
        given(memberRepository.findOneByEmail(member.getEmail().getAddress()))
                .willReturn(Optional.of(member));
        //when

        //then
        assertThrows(IllegalArgumentException.class, () -> memberService.join(memberDto));
    }

    @Test
    @DisplayName("사용자의 응모내역을 가져온다")
    void getMyDrawHistory() {
        //given
        String username = "test@gmail.com";
        Member member = mock(Member.class);
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, "1234!Basdf");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        given(memberRepository.findOneByEmail(username)).willReturn(Optional.of(member));

        //when
        when(member.getId()).thenReturn(1L);
        memberService.getMyDrawHistory();

        //then
        then(drawTicketService).should().findByMember(member.getId());
    }

    private MemberDto addTestMemberDto() {
        return MemberDto.builder()
                .name("testUser")
                .password("testPassword123!")
                .phoneNumber("01023451234")
                .birthday(LocalDate.now())
                .email("test@gmail.com")
                .address(new Address("seoul", "samsungro", "12345"))
                .gender(Gender.FEMALE)
                .build();
    }
}