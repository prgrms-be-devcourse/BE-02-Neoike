package prgrms.neoike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import prgrms.neoike.common.exception.EntityNotFoundException;
import prgrms.neoike.domain.member.Member;
import prgrms.neoike.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member 엔티티를 id 로 찾을 수 없습니다. memberID : " + memberId));
    }
}