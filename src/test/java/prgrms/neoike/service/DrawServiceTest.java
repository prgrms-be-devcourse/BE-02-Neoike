package prgrms.neoike.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.common.exception.TimeSequnceException;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.repository.DrawRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.LocalDateTime;


@SpringBootTest
@Transactional
class DrawServiceTest {
    @Autowired
    DrawService drawService;

    @Autowired
    DrawRepository drawRepository;

    @Test
    @DisplayName("Draw 엔티티를 저장한다.")
    void saveDraw () {
        // given
        LocalDateTime startDate = LocalDateTime.of(2022, 06, 12, 12, 00,00);
        LocalDateTime endDate = LocalDateTime.of(2022, 06, 13, 12, 00,00);
        LocalDateTime winningDate = LocalDateTime.of(2022, 06, 14, 12, 00,00);

        Draw draw = Draw.builder()
                .startDate(startDate)
                .endDate(endDate)
                .winningDate(winningDate)
                .quantity(50)
                .build();

        // when
        Long drawId = drawService.save(draw);

        // then
        assertThat(drawId).isEqualTo(1L);
    }

    @Test
    @DisplayName("등록 순서가 잘못된 Draw 엔티티를 저장할때 오류 발생")
    void invalidSaveDraw () {
        // given
        LocalDateTime fastDate = LocalDateTime.of(2022, 06, 12, 12, 00,00);
        LocalDateTime middleDate = LocalDateTime.of(2022, 06, 13, 12, 00,00);
        LocalDateTime lateDate = LocalDateTime.of(2022, 06, 14, 12, 00,00);

        // when // then
        assertThatThrownBy(() -> drawService.save(
                Draw.builder()
                        .startDate(lateDate)
                        .endDate(fastDate)
                        .winningDate(middleDate)
                        .quantity(50)
                        .build()
        )).isInstanceOf(TimeSequnceException.class);

        assertThatThrownBy(() -> drawService.save(
                Draw.builder()
                        .startDate(middleDate)
                        .endDate(lateDate)
                        .winningDate(fastDate)
                        .quantity(50)
                        .build()
        )).isInstanceOf(TimeSequnceException.class);
    }

}