package prgrms.neoike.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.domain.draw.DrawTicket;
import prgrms.neoike.domain.member.Member;
import prgrms.neoike.repository.DrawRepository;
import prgrms.neoike.repository.DrawTicketRepository;
import prgrms.neoike.repository.MemberRepository;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketResponse;
import prgrms.neoike.service.converter.DrawConverter;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DrawTicketServiceTest {
    @InjectMocks
    DrawTicketService drawTicketService;

    @Mock
    DrawRepository drawRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    DrawTicketRepository drawTicketRepository;

    @Mock
    DrawConverter drawConverter;

    @Test
    @DisplayName("DrawTicket 을 저장한다.")
    void saveDrwaTicket() {
        // given
        Draw drawMock = mock(Draw.class);
        Member memberMock = mock(Member.class);
        DrawTicket drawTicketMock = mock(DrawTicket.class);
        Draw reducedDrawMock = mock(Draw.class);
        DrawTicketResponse drawTicketResponse = new DrawTicketResponse(1L);

        given(drawMock.getId()).willReturn(1L);
        given(reducedDrawMock.getQuantity()).willReturn(49);
        given(drawMock.drawAndCheckSpare()).willReturn(true);

        given(memberRepository.findById(any())).willReturn(Optional.of(memberMock));
        given(drawRepository.findById(any())).willReturn(Optional.of(drawMock));
        given(drawTicketRepository.save(any(DrawTicket.class))).willReturn(drawTicketMock);
        given(drawConverter.toDrawTicketResponse(drawTicketMock.getId())).willReturn(drawTicketResponse);

        // when
        DrawTicketResponse drawTicketResponse1 = drawTicketService.saveDrawTicket(memberMock.getId(), drawMock.getId());

        // then
        assertThat(reducedDrawMock.getQuantity()).isEqualTo(49);
        assertThat(drawTicketResponse1.drawTicketId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Draw 의 재고가 0 이어서 DrawTicket 을 저장하지 못한다.")
    void invalidSaveDrwaTicket() {
        // given
        Draw drawMock = mock(Draw.class);
        Member memberMock = mock(Member.class);

        given(drawMock.getId()).willReturn(1L);
        given(drawMock.getQuantity()).willReturn(0);

        given(memberRepository.findById(any())).willReturn(Optional.of(memberMock));
        given(drawRepository.findById(any())).willReturn(Optional.of(drawMock));
        given(drawMock.drawAndCheckSpare()).willThrow(IllegalStateException.class);

        // when // then
        assertThat(drawMock.getQuantity()).isZero();
        assertThatThrownBy(() ->
                drawTicketService.saveDrawTicket(memberMock.getId(), drawMock.getId())
        ).isInstanceOf(IllegalStateException.class);
    }
}
