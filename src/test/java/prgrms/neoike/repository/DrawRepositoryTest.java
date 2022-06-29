package prgrms.neoike.repository;

import static java.text.MessageFormat.format;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import prgrms.neoike.common.config.JpaAuditingConfig;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.domain.sneaker.MemberCategory;
import prgrms.neoike.domain.sneaker.Sneaker;
import prgrms.neoike.domain.sneaker.SneakerCategory;
import prgrms.neoike.domain.sneaker.SneakerImage;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class DrawRepositoryTest {

    @Autowired
    private DrawRepository drawRepository;

    @Autowired
    private SneakerRepository sneakerRepository;

    @ParameterizedTest
    @CsvSource(value = {
        "2022-06-28T12:00:00.000, 2022-06-28T12:00:00.000, 4",
        "2022-06-28T12:00:00.000, 2022-06-28T14:01:00.000, 3",
        "2022-06-28T12:00:00.000, 2022-07-05T12:00:00.000, 0",
    })
    @DisplayName("응모 예정인 draw 리스트를 조회한다")
    void findAvailableDrawsTest(LocalDateTime startDate, LocalDateTime now, int expected) {
        // given
        List<Sneaker> sneakers = createTestSneakers();
        saveTestDrawsPerDay(startDate, sneakers);

        // when
        List<Draw> availableDraws = drawRepository.findAllByWinningDateAfter(now);

        // then
        assertThat(availableDraws).hasSize(expected);
    }

    private void saveTestDrawsPerDay(LocalDateTime startDate, List<Sneaker> sneakers) {
        List<Draw> draws = IntStream.range(0, sneakers.size())
            .mapToObj(i -> Draw.builder()
                .startDate(startDate.plusDays(i))
                .endDate(startDate.plusDays(i).plusHours(1))
                .winningDate(startDate.plusDays(i).plusHours(2))
                .sneaker(sneakers.get(i))
                .quantity(10)
                .build())
            .toList();

        drawRepository.saveAll(draws);
    }

    private List<Sneaker> createTestSneakers() {
        List<Sneaker> sneakers = IntStream.range(1, 5)
            .mapToObj(i -> Sneaker.builder()
                .memberCategory(MemberCategory.KIDS)
                .sneakerCategory(SneakerCategory.BASKETBALL)
                .name("조던 에어 " + i)
                .price(i * 10000)
                .description("조던 에어 " + i)
                .code("JO" + i)
                .releaseDate(LocalDateTime.now())
                .build())
            .toList();

        List<List<SneakerImage>> sneakerImagesPerSneakers = IntStream.range(1, 5)
            .mapToObj(sneakerId -> IntStream.range(1, 3)
                .mapToObj(imageId -> new SneakerImage(
                    format("jordan_air{0}_{1}.jpg", sneakerId, imageId)))
                .toList())
            .toList();

        IntStream.range(0, sneakers.size())
            .forEach(i -> sneakers.get(i).attachImages(sneakerImagesPerSneakers.get(i)));

        return sneakerRepository.saveAll(sneakers);
    }
}