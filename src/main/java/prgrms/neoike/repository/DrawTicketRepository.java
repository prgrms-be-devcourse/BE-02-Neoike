package prgrms.neoike.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.domain.draw.DrawTicket;
import prgrms.neoike.domain.member.Member;

import java.util.List;
import java.util.Optional;

public interface DrawTicketRepository extends JpaRepository<DrawTicket, Long> {
    Optional<DrawTicket> findByMemberAndDraw(Member member, Draw draw);

    List<DrawTicket> findByMember(Member member);

    List<DrawTicket> findByDraw(Draw draw);
}
