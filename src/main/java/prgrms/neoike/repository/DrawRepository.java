package prgrms.neoike.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import prgrms.neoike.domain.draw.Draw;
import java.util.Optional;

public interface DrawRepository extends JpaRepository<Draw, Long> {

    @Query("select d from Draw d join fetch d.sneaker where d.id = :id")
    Optional<Draw> findByIdWithSneakerItem(Long id);
}