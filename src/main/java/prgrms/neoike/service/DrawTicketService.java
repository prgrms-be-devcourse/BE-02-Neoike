package prgrms.neoike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.common.exception.EntityNotFoundException;
import prgrms.neoike.common.exception.InvalidDrawQuantityException;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.domain.draw.DrawTicket;
import prgrms.neoike.domain.member.Member;
import prgrms.neoike.repository.DrawRepository;
import prgrms.neoike.repository.DrawTicketRepository;
import prgrms.neoike.repository.MemberRepository;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketResponse;
import prgrms.neoike.service.mapper.ServiceDrawMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class DrawTicketService {
    private final ServiceDrawMapper serviceDrawMapper;
    private final DrawTicketRepository drawTicketRepository;
    private final DrawRepository drawRepository;
    private final MemberRepository memberRepository;

    public DrawTicketResponse saveDrawTicket(Long memberId, Long drawId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member 엔티티를 id 로 찾을 수 없습니다. memberId : " + drawId));

        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new EntityNotFoundException("Draw 엔티티를 id 로 찾을 수 없습니다. drawId : " + drawId));

        if (draw.drawAndCheckSpare()) {
            DrawTicket drawTicket = DrawTicket.builder()
                    .member(member)
                    .draw(draw)
                    .build();
            DrawTicket save = drawTicketRepository.save(drawTicket);

            return serviceDrawMapper.convertToDrawTicketResponse(save.getId());
        }
        throw new InvalidDrawQuantityException("draw 의 quantity 가 0 이어서 더이상 ticket 발행이 안됩니다. drawId : " + draw.getId());
    }

}
