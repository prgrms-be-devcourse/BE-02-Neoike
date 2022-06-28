package prgrms.neoike.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import prgrms.neoike.domain.draw.Draw;

public interface DrawRepository extends JpaRepository<Draw, Long> {

    @Query("select d from Draw d join fetch d.sneaker where d.id = :id")
    Optional<Draw> findByIdWithSneakerItem(Long id);

    @Query(
        """
            select distinct d from Draw d
            join fetch d.sneaker s
            left join fetch s.sneakerImages
            where d.winningDate > :now
        """
    )
    List<Draw> findAllByWinningDateAfter(LocalDateTime now);
}