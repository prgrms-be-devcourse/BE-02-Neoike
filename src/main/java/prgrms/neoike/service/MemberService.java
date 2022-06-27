package prgrms.neoike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.common.exception.EntityNotFoundException;
import prgrms.neoike.common.jwt.SecurityUtil;
import prgrms.neoike.domain.member.Member;
import prgrms.neoike.repository.MemberRepository;
import prgrms.neoike.service.converter.MemberConverter;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketsResponse;
import prgrms.neoike.service.dto.memberdto.MemberDto;
import prgrms.neoike.service.dto.memberdto.MemberResponse;

import java.util.Optional;

import static java.text.MessageFormat.format;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    private final DrawTicketService drawTicketService;

    @Transactional
    public MemberResponse join(MemberDto memberDto) {
        validateDuplicatedMember(memberDto.email());
        Member member = MemberConverter.toMember(memberDto);
        Member savedMember = memberRepository.save(member);

        return MemberConverter.toMemberResponse(savedMember.getId(), savedMember.getEmail().getEmail());
    }

    private void validateDuplicatedMember(String email) {
        Optional<Member> foundMember = memberRepository.findOneByEmail(email);
        if (foundMember.isPresent()) {
            throw new IllegalArgumentException(format("이미 존재하는 회원입니다. email : {0}", email));
        }
    }

    public DrawTicketsResponse getMyDrawHistory() {
        Optional<String> username = SecurityUtil.getCurrentUserName();
        Member member = username.flatMap(memberRepository::findOneByEmail)
                .orElseThrow(() -> new EntityNotFoundException(format("존재하지 않는 회원입니다. email : {0}", username)));

        return drawTicketService.findByMemberId(member.getId());
    }
}