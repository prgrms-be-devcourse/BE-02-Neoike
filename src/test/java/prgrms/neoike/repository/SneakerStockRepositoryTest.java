package prgrms.neoike.repository;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import prgrms.neoike.domain.sneaker.Sneaker;
import prgrms.neoike.domain.sneaker.SneakerStock;
import prgrms.neoike.domain.sneaker.Stock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static prgrms.neoike.domain.sneaker.MemberCategory.MEN;
import static prgrms.neoike.domain.sneaker.SneakerCategory.JORDAN;

@DataJpaTest
@EnableJpaAuditing
class SneakerStockRepositoryTest {

    @Autowired
    private SneakerStockRepository sneakerStockRepository;

    @Autowired
    private SneakerRepository sneakerRepository;

    private final SneakerStock sneakerStock = new SneakerStock(250, new Stock(10));

    @BeforeEach
    void setup() {
        Sneaker sneaker = createSneaker();
        sneakerRepository.save(sneaker);
        this.sneakerStock.setSneaker(sneaker);
    }

    @Test
    void testFindByIdAndCode() {
        SneakerStock savedSneakerStock = sneakerStockRepository.save(sneakerStock);

        Sneaker sneaker = savedSneakerStock.getSneaker();
        List<SneakerStock> sneakerStocks = sneakerStockRepository.findByIdAndCode(sneaker.getId(), sneaker.getCode());

        assertThat(sneakerStocks).isNotEmpty();
        assertThat(sneakerStocks.get(0).getSize()).isEqualTo(250);
        assertThat(sneakerStocks.get(0).getSneaker()).isEqualTo(sneaker);
    }

    @Test
    void testFindByIdAndSize() {
        SneakerStock savedSneakerStock = sneakerStockRepository.save(sneakerStock);

        Optional<SneakerStock> retrievedSneakerStock = sneakerStockRepository.findByIdAndSize(sneakerStock.getId(), sneakerStock.getSize());

        assertThat(retrievedSneakerStock).isNotEmpty();
        assertThat(retrievedSneakerStock.get().getId()).isEqualTo(savedSneakerStock.getId());
        assertThat(retrievedSneakerStock.get().getSize()).isEqualTo(savedSneakerStock.getSize());

    }

    private Sneaker createSneaker() {
        return Sneaker
            .builder()
            .memberCategory(MEN)
            .sneakerCategory(JORDAN)
            .name(RandomStringUtils.randomAlphabetic(5))
            .price(Integer.parseInt(RandomStringUtils.randomNumeric(5)))
            .description(RandomStringUtils.randomAlphanumeric(30))
            .code(RandomStringUtils.randomAlphanumeric(2).toUpperCase() + "-" + RandomStringUtils.randomNumeric(7))
            .releaseDate(LocalDateTime.now())
            .build();
    }
}