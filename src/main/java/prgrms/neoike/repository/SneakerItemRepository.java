package prgrms.neoike.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.domain.sneaker.SneakerItem;

import java.util.List;

public interface SneakerItemRepository extends JpaRepository<SneakerItem, Long> {

    List<SneakerItem> findByDraw(Draw draw);
}
