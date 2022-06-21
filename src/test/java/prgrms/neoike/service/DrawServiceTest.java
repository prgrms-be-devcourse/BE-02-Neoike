package prgrms.neoike.service;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import prgrms.neoike.controller.dto.drawdto.DrawItem;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.repository.DrawRepository;
import prgrms.neoike.service.dto.drawdto.DrawResponse;
import prgrms.neoike.service.dto.drawdto.ServiceDrawSaveDto;
import prgrms.neoike.service.converter.DrawConverter;

import javax.persistence.MapKeyColumn;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DrawServiceTest {
    @InjectMocks
    DrawService drawService;

    @Mock
    DrawRepository drawRepository;

    @Mock
    DrawConverter drawConverter;

    LocalDateTime startDate = LocalDateTime.of(2022, 06, 12, 12, 00, 00);
    LocalDateTime endDate = LocalDateTime.of(2022, 06, 13, 12, 00, 00);
    LocalDateTime winningDate = LocalDateTime.of(2022, 06, 14, 12, 00, 00);

    @Test
    @DisplayName("Draw 엔티티를 저장한다.")
    void saveDraw() {
        // given
        ServiceDrawSaveDto drawSaveDto = ServiceDrawSaveDto.builder()
                .sneakerId(1L)
                .startDate(startDate)
                .endDate(endDate)
                .winningDate(winningDate)
                .quantity(50)
                .sneakerItems(new ArrayList<DrawItem>() {{
                    add(new DrawItem(1L, 10));
                    add(new DrawItem(2L, 20));
                }})
                .build();

        Draw draw = mock(Draw.class);
        Draw drawEntity = mock(Draw.class);
        DrawResponse drawResponse = new DrawResponse(1L);

        given(drawConverter.toDraw(any(ServiceDrawSaveDto.class))).willReturn(draw);
        given(drawRepository.save(draw)).willReturn(drawEntity);
        given(drawConverter.toDrawResponseDto(drawEntity.getId())).willReturn(drawResponse);

        // when
        DrawResponse save = drawService.save(drawSaveDto);

        // then
        assertThat(save.drawId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("등록 순서가 잘못된 Draw 엔티티를 저장할때 오류 발생")
    void invalidSaveDraw1() {
        // given
        ServiceDrawSaveDto serviceDrawSaveDtoCase = ServiceDrawSaveDto.builder()
                .sneakerId(1L)
                .startDate(endDate)
                .endDate(startDate)
                .winningDate(winningDate)
                .quantity(50)
                .sneakerItems(new ArrayList<DrawItem>() {{
                    add(new DrawItem(1L, 10));
                    add(new DrawItem(2L, 20));
                }})
                .build();


        given(drawConverter.toDraw(serviceDrawSaveDtoCase)).willThrow(IllegalArgumentException.class);

        // when // then
        assertThatThrownBy(() -> drawService.save(
                serviceDrawSaveDtoCase
        )).isInstanceOf(IllegalArgumentException.class);
    }
}
