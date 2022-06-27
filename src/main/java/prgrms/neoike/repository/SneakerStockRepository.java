package prgrms.neoike.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prgrms.neoike.domain.sneaker.Sneaker;
import prgrms.neoike.domain.sneaker.SneakerStock;

import java.util.Optional;

public interface SneakerStockRepository extends JpaRepository<SneakerStock, Long> {

    Optional<SneakerStock> findBySneakerAndSize(Sneaker sneaker, int size);

}
