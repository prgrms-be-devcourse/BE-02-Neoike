package prgrms.neoike.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import prgrms.neoike.domain.sneaker.Sneaker;

import java.util.Optional;

public interface SneakerRepository extends JpaRepository<Sneaker, Long> {

    Optional<Sneaker> findByCode(String code);

    @Override
    @Query(value = "select s from Sneaker s",
        countQuery = "select count(s) from Sneaker s")
    Page<Sneaker> findAll(Pageable pageable);
}