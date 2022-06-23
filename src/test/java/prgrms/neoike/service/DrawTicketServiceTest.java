package prgrms.neoike.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.domain.member.*;
import prgrms.neoike.repository.DrawRepository;
import prgrms.neoike.repository.DrawTicketRepository;
import prgrms.neoike.repository.MemberRepository;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketListResponse;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
class DrawTicketServiceTest {
    @Autowired
    DrawTicketService drawTicketService;

    @Autowired
    DrawTicketRepository drawTicketRepository;

    @Autowired
    DrawRepository drawRepository;

    @Autowired
    MemberRepository memberRepository;

    LocalDateTime startDate = LocalDateTime.of(2025, 06, 12, 12, 00, 00);
    LocalDateTime endDate = LocalDateTime.of(2025, 06, 13, 12, 00, 00);
    LocalDateTime winningDate = LocalDateTime.of(2025, 06, 14, 12, 00, 00);

    @AfterEach
    void afterEach() {
        drawTicketRepository.deleteAllInBatch();
        drawRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("DrawTicket 을 저장한다.")
    @Transactional
    void saveDrawTicketTest() {
        // given
        Draw draw = drawRepository.save(sampleDraw(50));
        Member member = memberRepository.save(sampleMember());

        // when
        drawTicketService.save(member.getId(), draw.getId());

        // then
        Draw reducedDraw = drawRepository.findById(draw.getId()).get();
        assertThat(reducedDraw.getQuantity()).isEqualTo(49);
    }

    @Test
    @DisplayName("memberID 로 DrawTicket을 조회한다.")
    @Transactional
    void findDrawTicketsByMemberIdTest() {
        // given
        Draw draw1 = drawRepository.save(sampleDraw(50));
        Draw draw2 = drawRepository.save(sampleDraw(30));

        Member member = memberRepository.save(sampleMember());

        // when
        drawTicketService.save(member.getId(), draw1.getId());
        drawTicketService.save(member.getId(), draw2.getId());

        // then
        DrawTicketListResponse drawTicketResponses = drawTicketService.findByMember(member.getId());

        assertThat(drawTicketResponses.drawTicketResponses().size()).isEqualTo(2);
        // response 에 받는 정보가 추가되면 테스트 코드 추가 예정
    }

    @Test
    @DisplayName("Draw 의 재고가 0 이어서 DrawTicket 을 저장하지 못한다.")
    @Transactional
    void invalidSaveDrawTicketTest() {
        // given
        Draw draw = drawRepository.save(sampleDraw(0));
        Member member = memberRepository.save(sampleMember());

        // when // then
        assertThatThrownBy(() ->
                drawTicketService.save(member.getId(), draw.getId())
        ).isInstanceOf(IllegalStateException.class);
    }


    @Test
    @DisplayName("이미 Draw 에 응모를 한사람은 재응모가 불가능하다")
    @Transactional
    void duplicateSaveDrawTicketTest() {
        // given
        Draw draw = drawRepository.save(sampleDraw(50));
        Member member = memberRepository.save(sampleMember());

        // when
        drawTicketService.save(member.getId(), draw.getId());

        // then
        assertThatThrownBy(() ->
                drawTicketService.save(member.getId(), draw.getId())
        ).isInstanceOf(IllegalStateException.class);
    }

    private Draw sampleDraw(int quantity) {
        return Draw.builder()
                .startDate(startDate)
                .endDate(endDate)
                .winningDate(winningDate)
                .quantity(quantity)
                .build();
    }

    private Member sampleMember() {
        return Member.builder()
                .name("이용훈")
                .password(new Password("123abcAB!!"))
                .phoneNumber(new PhoneNumber(CountryType.KOR, "01012341566"))
                .address(new Address("도시", "거리", "000222"))
                .birthDay(LocalDateTime.now())
                .email(new Email("test@test.com"))
                .gender(Gender.MALE)
                .build();
    }
}