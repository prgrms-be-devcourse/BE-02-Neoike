package prgrms.neoike.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import prgrms.neoike.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

}