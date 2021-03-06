package prgrms.neoike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.domain.member.Member;
import prgrms.neoike.repository.MemberRepository;
import prgrms.neoike.service.converter.MemberConverter;
import prgrms.neoike.service.dto.member.MemberDto;
import prgrms.neoike.service.dto.member.MemberResponse;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketsResponse;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static java.text.MessageFormat.format;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final DrawTicketService drawTicketService;

    @Transactional
    public MemberResponse join(MemberDto memberDto) {
        validateDuplicatedMember(memberDto.email());
        Member member = MemberConverter.toMember(memberDto);
        Member savedMember = memberRepository.save(member);

        return MemberConverter.toMemberResponse(savedMember.getId(), savedMember.getEmail());
    }

    @Transactional(readOnly = true)
    public DrawTicketsResponse getMyDrawHistory(String email) {
        Member member = memberRepository.findOneByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException(format("존재하지 않는 회원입니다. email : {0}", email)));

        return drawTicketService.findByMemberId(member.getId());
    }

    private void validateDuplicatedMember(String email) {
        Optional<Member> foundMember = memberRepository.findOneByEmail(email);
        foundMember.ifPresent(member -> {
            throw new IllegalArgumentException(format("이미 존재하는 회원입니다. email : {0}", email));
        });
    }
}