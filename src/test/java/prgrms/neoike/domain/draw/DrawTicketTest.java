package prgrms.neoike.domain.draw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import prgrms.neoike.domain.member.Member;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DrawTicketTest {

    @Test
    @DisplayName("drawTicket을 생성한다")
    void creatDrawTest() {
        // given
        LocalDateTime fastDate = LocalDateTime.of(2022, 06, 12, 12, 00, 00);
        LocalDateTime middleDate = LocalDateTime.of(2022, 06, 13, 12, 00, 00);
        LocalDateTime lastDate = LocalDateTime.of(2022, 06, 14, 12, 00, 00);

        Draw draw = Draw.builder()
            .startDate(fastDate)
            .endDate(middleDate)
            .winningDate(lastDate)
            .quantity(50)
            .build();

        Member member = Member.builder()
            .name("이용훈")
            .build();

        // when
        DrawTicket drawTicket = DrawTicket.builder()
            .member(member)
            .draw(draw)
            .sneakerName("airjordan")
            .price(27500)
            .code("AB1234")
            .size(275)
            .build();

        // then
        assertEquals(drawTicket.getDraw().getStartDate(), fastDate);
        assertEquals(drawTicket.getDraw().getEndDate(), middleDate);
        assertEquals(drawTicket.getDraw().getWinningDate(), lastDate);
        assertEquals(drawTicket.getDraw().getQuantity(), 50);
        assertEquals(drawTicket.getMember().getName(), "이용훈");
        assertEquals(drawTicket.getDrawStatus(), DrawStatus.WAITING);
        assertEquals(drawTicket.getSneakerName(), "airjordan");
        assertEquals(drawTicket.getPrice(), 27500);
        assertEquals(drawTicket.getCode(), "AB1234");
        assertEquals(drawTicket.getSize(), 275);
    }
}