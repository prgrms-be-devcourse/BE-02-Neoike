package prgrms.neoike.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.common.exception.InvalidDrawQuantityException;
import prgrms.neoike.common.exception.TimeSequnceException;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.domain.member.Member;
import prgrms.neoike.repository.DrawTicketRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
class DrawTicketServiceTest {
    @Autowired
    DrawTicketService drawTicketService;

    @Autowired
    DrawTicketRepository drawTicketRepository;

    @Test
    @DisplayName("DrawTicket 을 저장한다.")
    void saveDrwaTicket () throws Exception {
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

        Member member = Member.builder()
                .build();

        // when
        Long drawTicketId = drawTicketService.saveDrawTicket(member, draw);

        // then
        assertThat(draw.getQuantity()).isEqualTo(49);
    }

    @Test
    @DisplayName("Draw 의 재고가 0 이어서 DrawTicket 을 저장하지 못한다.")
    void invalidSaveDrwaTicket () throws Exception {
        // given
        LocalDateTime startDate = LocalDateTime.of(2022, 06, 12, 12, 00,00);
        LocalDateTime endDate = LocalDateTime.of(2022, 06, 13, 12, 00,00);
        LocalDateTime winningDate = LocalDateTime.of(2022, 06, 14, 12, 00,00);

        Draw draw = Draw.builder()
                .startDate(startDate)
                .endDate(endDate)
                .winningDate(winningDate)
                .quantity(0)
                .build();
        Member member = Member.builder()
                .build();

        // when // then

        assertThatThrownBy(() ->
                drawTicketService.saveDrawTicket(member, draw)
        ).isInstanceOf(InvalidDrawQuantityException.class);
    }

}