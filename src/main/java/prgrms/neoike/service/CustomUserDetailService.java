package prgrms.neoike.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.common.exception.EntityNotFoundException;
import prgrms.neoike.domain.member.Member;
import prgrms.neoike.repository.MemberRepository;

import java.util.List;

import static java.text.MessageFormat.format;

@Component("userDetailsService")
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return memberRepository.findOneByEmail(username)
                .map(this::createUser)
                .orElseThrow(() -> new EntityNotFoundException(format("회원을 찾을 수 없습니다. email : {0}", username)));
    }

    private User createUser(Member member) {
        return new User(member.getEmail().getEmail(), member.getPassword().getPassword(), List.of());
    }
}
