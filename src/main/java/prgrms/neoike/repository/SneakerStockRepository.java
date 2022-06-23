package prgrms.neoike.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prgrms.neoike.domain.sneaker.SneakerStock;

public interface SneakerStockRepository extends JpaRepository<SneakerStock, Long> {
}
