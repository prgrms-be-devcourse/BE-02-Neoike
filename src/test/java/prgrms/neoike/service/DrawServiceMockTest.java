package prgrms.neoike.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import prgrms.neoike.controller.dto.drawdto.DrawItem;
import prgrms.neoike.controller.dto.drawdto.DrawSaveRequest;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.repository.DrawRepository;
import prgrms.neoike.service.dto.drawdto.DrawResponse;
import prgrms.neoike.service.dto.drawdto.ServiceDrawSaveDto;
import prgrms.neoike.service.mapper.ServiceDrawMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DrawServiceMockTest {
    @InjectMocks
    DrawService drawService;

    @Mock
    DrawRepository drawRepository;

    @Mock
    ServiceDrawMapper serviceDrawMapper;

    @Test
    @DisplayName("Draw 엔티티를 저장한다.")
    void saveDraw() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2022, 06, 12, 12, 00, 00);
        LocalDateTime endDate = LocalDateTime.of(2022, 06, 13, 12, 00, 00);
        LocalDateTime winningDate = LocalDateTime.of(2022, 06, 14, 12, 00, 00);

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

        Draw draw = new Draw(startDate, endDate, winningDate, 50);
        DrawResponse drawResponse = new DrawResponse(1L);

        given(serviceDrawMapper.convertoDraw(any(ServiceDrawSaveDto.class))).willReturn(draw);
        given(drawRepository.save(any(Draw.class))).willReturn(draw);
        given(serviceDrawMapper.convertToDrawResponseDto(any())).willReturn(drawResponse);

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

        ServiceDrawSaveDto serviceDrawSaveDtoCase1 = ServiceDrawSaveDto.builder()
                .sneakerId(1L)
                .startDate(lateDate)
                .endDate(fastDate)
                .winningDate(middleDate)
                .quantity(50)
                .sneakerItems(new ArrayList<DrawItem>() {{
                    add(new DrawItem(1L, 10));
                    add(new DrawItem(2L, 20));
                }})
                .build();

        ServiceDrawSaveDto serviceDrawSaveDtoCase2 = ServiceDrawSaveDto.builder()
                .sneakerId(1L)
                .startDate(middleDate)
                .endDate(lateDate)
                .winningDate(fastDate)
                .quantity(50)
                .sneakerItems(new ArrayList<DrawItem>() {{
                    add(new DrawItem(1L, 10));
                    add(new DrawItem(2L, 20));
                }})
                .build();

        when(serviceDrawMapper.convertoDraw(serviceDrawSaveDtoCase1)).thenThrow(IllegalArgumentException.class);
        when(serviceDrawMapper.convertoDraw(serviceDrawSaveDtoCase2)).thenThrow(IllegalArgumentException.class);

        // when // then
        assertThatThrownBy(() -> drawService.save(
                serviceDrawSaveDtoCase1
        )).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> drawService.save(
                serviceDrawSaveDtoCase2
        )).isInstanceOf(IllegalArgumentException.class);
    }
}
