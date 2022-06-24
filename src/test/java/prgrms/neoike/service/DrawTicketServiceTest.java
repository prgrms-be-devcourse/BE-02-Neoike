package prgrms.neoike.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.domain.draw.DrawStatus;
import prgrms.neoike.domain.member.*;
import prgrms.neoike.domain.sneaker.MemberCategory;
import prgrms.neoike.domain.sneaker.Sneaker;
import prgrms.neoike.domain.sneaker.SneakerCategory;
import prgrms.neoike.repository.DrawRepository;
import prgrms.neoike.repository.DrawTicketRepository;
import prgrms.neoike.repository.MemberRepository;
import prgrms.neoike.repository.SneakerRepository;
import prgrms.neoike.service.dto.drawdto.ServiceDrawSaveDto;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketsResponse;

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

    @Autowired
    SneakerRepository sneakerRepository;

    LocalDateTime startDate = LocalDateTime.of(2025, 06, 12, 12, 00, 00);
    LocalDateTime endDate = LocalDateTime.of(2025, 06, 13, 12, 00, 00);
    LocalDateTime winningDate = LocalDateTime.of(2025, 06, 14, 12, 00, 00);

    @AfterEach
    void afterEach() {
        drawTicketRepository.deleteAllInBatch();
        drawRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        sneakerRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("DrawTicket 을 저장한다.")
    @Transactional
    void saveDrawTicketTest() {
        // given
        Sneaker sneaker = sneakerRepository.save(sneaker());
        Draw draw = drawRepository.save(validDraw(50, sneaker));
        Member member = memberRepository.save(validMember());

        // when
        drawTicketService.save(member.getId(), draw.getId(), 275);

        // then
        Draw reducedDraw = drawRepository.findById(draw.getId()).get();
        assertThat(reducedDraw.getQuantity()).isEqualTo(49);
    }

    @Test
    @DisplayName("memberID 로 DrawTicket을 조회한다.")
    @Transactional
    void findDrawTicketsByMemberIdTest() {
        // given
        Sneaker sneaker1 = sneakerRepository.save(sneaker());
        Sneaker sneaker2 = sneakerRepository.save(sneaker());

        Draw draw1 = drawRepository.save(validDraw(50, sneaker1));
        Draw draw2 = drawRepository.save(validDraw(30, sneaker2));
        Member member = memberRepository.save(validMember());

        // when
        drawTicketService.save(member.getId(), draw1.getId(), 275);
        drawTicketService.save(member.getId(), draw2.getId(), 275);

        // then
        DrawTicketsResponse drawTicketResponses = drawTicketService.findByMember(member.getId());

        assertThat(drawTicketResponses.drawTicketResponses()).hasSize(2);
        assertThat(drawTicketResponses.drawTicketResponses().get(0).drawStatus()).isEqualTo(DrawStatus.WAITING);
        assertThat(drawTicketResponses.drawTicketResponses().get(0).sneakerName()).isEqualTo("air jordan");
        assertThat(drawTicketResponses.drawTicketResponses().get(0).price()).isEqualTo(75000);
        assertThat(drawTicketResponses.drawTicketResponses().get(0).code()).isEqualTo("AB1234");
        assertThat(drawTicketResponses.drawTicketResponses().get(0).size()).isEqualTo(275);
        // response 에 받는 정보가 추가되면 테스트 코드 추가 예정
    }

    @Test
    @DisplayName("Draw 의 재고가 0 이어서 DrawTicket 을 저장하지 못한다.")
    @Transactional
    void invalidSaveDrawTicketTest() {
        // given
        Sneaker sneaker = sneakerRepository.save(sneaker());
        Draw draw = drawRepository.save(validDraw(0, sneaker));
        Member member = memberRepository.save(validMember());

        // when // then
        assertThatThrownBy(() ->
                drawTicketService.save(member.getId(), draw.getId(), 275)
        ).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("이미 Draw 에 응모를 한사람은 재응모가 불가능하다")
    @Transactional
    void duplicateSaveDrawTicketTest() {
        // given
        Sneaker sneaker = sneakerRepository.save(sneaker());
        Draw draw = drawRepository.save(validDraw(50, sneaker));
        Member member = memberRepository.save(validMember());

        // when
        drawTicketService.save(member.getId(), draw.getId(), 275);

        // then
        assertThatThrownBy(() ->
                drawTicketService.save(member.getId(), draw.getId(), 275)
        ).isInstanceOf(IllegalStateException.class);
    }

    private Draw validDraw(int quantity, Sneaker sneaker) {
        return Draw.builder()
                .sneaker(sneaker)
                .startDate(startDate)
                .endDate(endDate)
                .winningDate(winningDate)
                .quantity(quantity)
                .build();
    }

    private Sneaker sneaker() {
        return Sneaker.builder()
                .memberCategory(MemberCategory.MEN)
                .sneakerCategory(SneakerCategory.BASKETBALL)
                .name("air jordan")
                .price(75000)
                .description("1")
                .code("AB1234")
                .releaseDate(LocalDateTime.now())
                .build();
    }

    private Member validMember() {
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