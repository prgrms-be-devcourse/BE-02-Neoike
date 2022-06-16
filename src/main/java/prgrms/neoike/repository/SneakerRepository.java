package prgrms.neoike.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prgrms.neoike.domain.sneaker.Sneaker;

public interface SneakerRepository extends JpaRepository<Sneaker, Long> {

}