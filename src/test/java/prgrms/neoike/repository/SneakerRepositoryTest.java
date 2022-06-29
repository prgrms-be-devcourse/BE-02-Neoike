package prgrms.neoike.repository;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import prgrms.neoike.domain.sneaker.Sneaker;
import prgrms.neoike.domain.sneaker.SneakerImage;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static prgrms.neoike.domain.sneaker.MemberCategory.MEN;
import static prgrms.neoike.domain.sneaker.SneakerCategory.JORDAN;

@DataJpaTest
@EnableJpaAuditing
class SneakerRepositoryTest {

    @Autowired
    private SneakerRepository sneakerRepository;

    @Test
    @DisplayName("전체 신발 리스트를 페이징 처리하여 가져온다.")
    void testFindAll() {
        sneakerRepository.saveAll(
            List.of(
                createRandomSneaker(),
                createRandomSneaker(),
                createRandomSneaker(),
                createRandomSneaker(),
                createRandomSneaker()
            )
        );

        PageRequest pageRequest = PageRequest.of(0, 3);
        Page<Sneaker> sneakers = sneakerRepository.findAll(pageRequest);

        assertThat(sneakers.getNumber()).isZero();
        assertThat(sneakers.getSize()).isEqualTo(3);
        assertThat(sneakers.getTotalElements()).isEqualTo(5);
        assertThat(sneakers.isFirst()).isTrue();
        assertThat(sneakers.isLast()).isFalse();
        assertThat(sneakers.getContent().get(0).getSneakerImages()).isNotEmpty();
    }

    private Sneaker createRandomSneaker() {
        Sneaker sneaker = Sneaker
            .builder()
            .memberCategory(MEN)
            .sneakerCategory(JORDAN)
            .name(RandomStringUtils.randomAlphabetic(5))
            .price(Integer.parseInt(RandomStringUtils.randomNumeric(5)))
            .description(RandomStringUtils.randomAlphanumeric(30))
            .code(RandomStringUtils.randomAlphanumeric(2).toUpperCase() + "-" + RandomStringUtils.randomNumeric(7))
            .releaseDate(LocalDateTime.now())
            .build();

        sneaker.attachImages(List.of(new SneakerImage("/src/main/" + randomUUID() + ".PNG")));

        return sneaker;
    }
}