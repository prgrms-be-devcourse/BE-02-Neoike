package prgrms.neoike.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import prgrms.neoike.domain.sneaker.SneakerStock;

import java.util.List;
import java.util.Optional;

public interface SneakerStockRepository extends JpaRepository<SneakerStock, Long> {

    @Query(
        """
            select s from SneakerStock s
            join fetch s.sneaker n
            left join fetch n.sneakerImages
            where n.id = :sneakerId and n.code = :code
        """
    )
    List<SneakerStock> findByIdAndCode(Long sneakerId, String code);

    @EntityGraph(attributePaths = "sneaker")
    Optional<SneakerStock> findByIdAndSize(Long id, int size);
}
