package prgrms.neoike.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.service.dto.drawdto.DrawResponse;
import prgrms.neoike.service.dto.drawdto.ServiceDrawSaveDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.ArrayList;


@SpringBootTest
@Transactional
class DrawServiceTest {
    @Autowired
    DrawService drawService;

    @Test
    @DisplayName("Draw 엔티티를 저장한다.")
    void saveDraw() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2022, 06, 12, 12, 00, 00);
        LocalDateTime endDate = LocalDateTime.of(2022, 06, 13, 12, 00, 00);
        LocalDateTime winningDate = LocalDateTime.of(2022, 06, 14, 12, 00, 00);

        ServiceDrawSaveDto drawSaveDto = new ServiceDrawSaveDto(
                1L, startDate, endDate, winningDate, 50, new ArrayList<>()
        );

        // when
        DrawResponse save = drawService.save(drawSaveDto);

        // then
        assertThat(save.drawId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("등록 순서가 잘못된 Draw 엔티티를 저장할때 오류 발생")
    void invalidSaveDraw() {
        // given
        LocalDateTime fastDate = LocalDateTime.of(2022, 06, 12, 12, 00, 00);
        LocalDateTime middleDate = LocalDateTime.of(2022, 06, 13, 12, 00, 00);
        LocalDateTime lateDate = LocalDateTime.of(2022, 06, 14, 12, 00, 00);

        // when // then
        assertThatThrownBy(() -> drawService.save(
                new ServiceDrawSaveDto(
                        1L, lateDate, fastDate, middleDate, 50, new ArrayList<>()
                )
        )).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> drawService.save(
                new ServiceDrawSaveDto(
                        1L, middleDate, lateDate, fastDate, 50, new ArrayList<>()
                )
        )).isInstanceOf(IllegalArgumentException.class);
    }

}