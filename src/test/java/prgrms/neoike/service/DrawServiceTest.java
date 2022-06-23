package prgrms.neoike.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.repository.DrawRepository;
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

    LocalDateTime startDate = LocalDateTime.of(2025, 06, 12, 12, 00, 00);
    LocalDateTime endDate = LocalDateTime.of(2025, 06, 13, 12, 00, 00);
    LocalDateTime winningDate = LocalDateTime.of(2025, 06, 14, 12, 00, 00);


    @Test
    @DisplayName("Draw 엔티티를 저장한다.")
    void saveDrawTest() {
        // given
        ServiceDrawSaveDto drawSaveDto = ServiceDrawSaveDto.builder()
                .sneakerId(1L)
                .startDate(startDate)
                .endDate(endDate)
                .winningDate(winningDate)
                .quantity(50)
                .sneakerItems(new ArrayList<>())
                .build();

        // when
        DrawResponse save = drawService.save(drawSaveDto);

        // then
        assertThat(save.drawId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("등록 순서가 잘못된 Draw 엔티티를 저장할때 오류 발생")
    void invalidSaveDrawTest() {
        // given // when // then
        assertThatThrownBy(() -> drawService.save(
                ServiceDrawSaveDto.builder()
                        .sneakerId(1L)
                        .startDate(endDate)
                        .endDate(startDate)
                        .winningDate(winningDate)
                        .quantity(50)
                        .sneakerItems(new ArrayList<>())
                        .build()
        )).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> drawService.save(
                ServiceDrawSaveDto.builder()
                        .sneakerId(1L)
                        .startDate(startDate)
                        .endDate(winningDate)
                        .winningDate(endDate)
                        .quantity(50)
                        .sneakerItems(new ArrayList<>())
                        .build()
        )).isInstanceOf(IllegalArgumentException.class);
    }
}