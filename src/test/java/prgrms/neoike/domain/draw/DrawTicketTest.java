package prgrms.neoike.domain.draw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import prgrms.neoike.domain.member.Member;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DrawTicketTest {
    @Test
    @DisplayName("drawTicket을 생성한다")
    void creatDrawTest () {
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
                .draw(draw)
                .member(member)
                .build();

        // then
        assertEquals(drawTicket.getDraw().getStartDate(), fastDate);
        assertEquals(drawTicket.getDraw().getEndDate(), middleDate);
        assertEquals(drawTicket.getDraw().getWinningDate(), lastDate);
        assertEquals(drawTicket.getDraw().getQuantity(), 50);
        assertEquals(drawTicket.getMember().getName(), "이용훈");
        assertEquals(drawTicket.getDrawStatus(), DrawStatus.WAITING);
    }
}