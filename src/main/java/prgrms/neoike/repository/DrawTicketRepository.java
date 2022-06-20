package prgrms.neoike.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prgrms.neoike.domain.draw.DrawTicket;

public interface DrawTicketRepository extends JpaRepository<DrawTicket, Long> {
}
