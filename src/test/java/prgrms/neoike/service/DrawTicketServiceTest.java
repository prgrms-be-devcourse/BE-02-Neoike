package prgrms.neoike.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.common.exception.InvalidDrawQuantityException;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.domain.member.Member;
import prgrms.neoike.repository.DrawRepository;
import prgrms.neoike.repository.MemberRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
class DrawTicketServiceTest {
    @Autowired
    DrawTicketService drawTicketService;

    @Autowired
    DrawRepository drawRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("DrawTicket 을 저장한다.")
    void saveDrwaTicket() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2022, 06, 12, 12, 00, 00);
        LocalDateTime endDate = LocalDateTime.of(2022, 06, 13, 12, 00, 00);
        LocalDateTime winningDate = LocalDateTime.of(2022, 06, 14, 12, 00, 00);

        Draw draw = drawRepository.save(
                Draw.builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .winningDate(winningDate)
                        .quantity(50)
                        .build()
        );

        Member member = memberRepository.save(
                Member.builder().build()
        );

        // when
        drawTicketService.saveDrawTicket(member.getId(), draw.getId());

        // then
        Draw reducedDraw = drawRepository.findById(draw.getId()).get();
        assertThat(reducedDraw.getQuantity()).isEqualTo(49);
    }

    @Test
    @DisplayName("Draw 의 재고가 0 이어서 DrawTicket 을 저장하지 못한다.")
    void invalidSaveDrwaTicket() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2022, 06, 12, 12, 00, 00);
        LocalDateTime endDate = LocalDateTime.of(2022, 06, 13, 12, 00, 00);
        LocalDateTime winningDate = LocalDateTime.of(2022, 06, 14, 12, 00, 00);

        Draw draw = drawRepository.save(
                Draw.builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .winningDate(winningDate)
                        .quantity(0)
                        .build()
        );

        Member member = memberRepository.save(
                Member.builder().build()
        );

        // when // then
        assertThatThrownBy(() ->
                drawTicketService.saveDrawTicket(member.getId(), draw.getId())
        ).isInstanceOf(InvalidDrawQuantityException.class);
    }

}