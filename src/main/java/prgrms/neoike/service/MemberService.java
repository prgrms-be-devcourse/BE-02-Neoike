package prgrms.neoike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.common.jwt.TokenProvider;
import prgrms.neoike.domain.member.Member;
import prgrms.neoike.repository.MemberRepository;
import prgrms.neoike.service.converter.MemberConverter;
import prgrms.neoike.service.dto.memberdto.LoginDto;
import prgrms.neoike.service.dto.memberdto.MemberDto;
import prgrms.neoike.service.dto.memberdto.MemberResponse;


import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final TokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponse join(MemberDto memberDto) {
        validateDuplicatedMember(memberDto.email());
        Member member = MemberConverter.toMember(memberDto);
        Member savedMember = memberRepository.save(member);

        return MemberConverter.toMemberResponse(savedMember.getId(), savedMember.getEmail().getEmail());
    }

    @Transactional(readOnly = true)
    public String login(LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password());

        Authentication authentication
                = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return tokenProvider.createToken(authentication);
    }

    private void validateDuplicatedMember(String email) {
        Optional<Member> foundMember = memberRepository.findOneByEmail(email);
        if (foundMember.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
    }
}