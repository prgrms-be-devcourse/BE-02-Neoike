package prgrms.neoike.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prgrms.neoike.domain.sneaker.Sneaker;

import java.util.Optional;

public interface SneakerRepository extends JpaRepository<Sneaker, Long> {

    Optional<Sneaker> findByCodeOrName(String code, String name);
}