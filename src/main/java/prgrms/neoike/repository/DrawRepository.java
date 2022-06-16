package prgrms.neoike.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prgrms.neoike.domain.draw.Draw;

public interface DrawRepository extends JpaRepository<Draw, Long> {

}