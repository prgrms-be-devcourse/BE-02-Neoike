package prgrms.neoike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.common.exception.InvalidDrawQuantityException;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.domain.draw.DrawTicket;
import prgrms.neoike.domain.member.Member;
import prgrms.neoike.repository.DrawTicketRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class DrawTicketService {
    private final DrawTicketRepository drawTicketRepository;

    public Long saveDrawTicket(Member member, Draw draw) {
        if (draw.checkPossibility()) {
            DrawTicket drawTicket = DrawTicket.builder()
                    .member(member)
                    .draw(draw)
                    .build();
            DrawTicket save = drawTicketRepository.save(drawTicket);

            return save.getId();
        }
        throw new InvalidDrawQuantityException("draw 의 quantity 가 0 이어서 더이상 ticket 발행이 안됩니다. drawId : " + draw.getId());
    }

}
