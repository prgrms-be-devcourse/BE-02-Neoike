package prgrms.neoike.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.domain.draw.DrawStatus;
import prgrms.neoike.domain.member.*;
import prgrms.neoike.domain.sneaker.*;
import prgrms.neoike.repository.*;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketsResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.text.MessageFormat.format;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
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

    @Autowired
    SneakerStockRepository sneakerStockRepository;

    @Autowired
    SneakerItemRepository sneakerItemRepository;

    LocalDateTime startDate = LocalDateTime.of(2025, 06, 12, 12, 00, 00);
    LocalDateTime endDate = LocalDateTime.of(2025, 06, 13, 12, 00, 00);
    LocalDateTime winningDate = LocalDateTime.of(2025, 06, 14, 12, 00, 00);

    @AfterEach
    void afterEach() {
        drawTicketRepository.deleteAll();
        drawRepository.deleteAll();
        memberRepository.deleteAll();
        sneakerRepository.deleteAll();
        sneakerStockRepository.deleteAll();
        sneakerItemRepository.deleteAll();
    }

    @Test
    @DisplayName("DrawTicket 을 저장한다.")
    void saveDrawTicketTest() {
        // given
        Sneaker sneaker = sneakerRepository.save(validSneaker());
        Draw draw = drawRepository.save(validDraw(70, sneaker));

        Member member1 = memberRepository.save(validMember());
        Member member2 = memberRepository.save(validMember());

        SneakerStock sneakerStock275 = sneakerStockRepository.save(validStock(sneaker, 275, 30));
        sneakerItemRepository.save(validItem(sneakerStock275, draw, 275, 20));

        SneakerStock sneakerStock285 = sneakerStockRepository.save(validStock(sneaker, 285, 20));
        sneakerItemRepository.save(validItem(sneakerStock285, draw, 285, 10));

        // when
        drawTicketService.save(member1.getId(), draw.getId(), 275);
        drawTicketService.save(member2.getId(), draw.getId(), 285);
        Draw reducedDraw = drawRepository.findById(draw.getId()).get();

        // then
        assertThat(reducedDraw.getQuantity()).isEqualTo(68);
    }

    @Test
    @DisplayName("한 회원은 중복으로 응모에 신청할 수 없다")
    void invalidSaveDrawTicketTest1() {
        // given
        Sneaker sneaker = sneakerRepository.save(validSneaker());
        Draw draw = drawRepository.save(validDraw(70, sneaker));
        Member member = memberRepository.save(validMember());

        SneakerStock sneakerStock275 = sneakerStockRepository.save(validStock(sneaker, 275, 30));
        sneakerItemRepository.save(validItem(sneakerStock275, draw, 275, 20));

        SneakerStock sneakerStock285 = sneakerStockRepository.save(validStock(sneaker, 285, 20));
        sneakerItemRepository.save(validItem(sneakerStock285, draw, 285, 10));

        // when
        drawTicketService.save(member.getId(), draw.getId(), 275);

        // then
        assertThatThrownBy(() ->
            drawTicketService.save(member.getId(), draw.getId(), 285)
        )
            .hasMessageContaining("티켓응모를 이미 진행하였습니다.")
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Draw 의 재고가 0 이어서 DrawTicket 을 저장하지 못한다.")
    void invalidSaveDrawTicketTest3() {
        // given
        Sneaker sneaker = sneakerRepository.save(validSneaker());
        Draw draw = drawRepository.save(validDraw(0, sneaker));
        Member member = memberRepository.save(validMember());

        SneakerStock sneakerStock275 = sneakerStockRepository.save(validStock(sneaker, 275, 30));
        sneakerItemRepository.save(validItem(sneakerStock275, draw, 275, 0));

        // when // then
        assertThatThrownBy(() ->
            drawTicketService.save(member.getId(), draw.getId(), 275)
        )
            .hasMessageContaining(
                format("draw 의 수량이 0 이어서 더이상 ticket 발행이 안됩니다. drawId : {0}", draw.getId()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("이미 Draw 에 응모를 한사람은 재응모가 불가능하다")
    void duplicateSaveDrawTicketTest() {
        // given
        Sneaker sneaker = sneakerRepository.save(validSneaker());
        Draw draw = drawRepository.save(validDraw(50, sneaker));
        Member member = memberRepository.save(validMember());

        SneakerStock sneakerStock275 = sneakerStockRepository.save(validStock(sneaker, 275, 30));
        sneakerItemRepository.save(validItem(sneakerStock275, draw, 275, 20));

        // when
        drawTicketService.save(member.getId(), draw.getId(), 275);

        // then
        assertThatThrownBy(() ->
            drawTicketService.save(member.getId(), draw.getId(), 275)
        )
            .hasMessageContaining("티켓응모를 이미 진행하였습니다.")
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("stock 의 사이즈와 입력된 size 가 다를경우 응모가 불가능하다.")
    void invalidSaveDrawTicketTest5() {
        // given
        Sneaker sneaker = sneakerRepository.save(validSneaker());
        Draw draw = drawRepository.save(validDraw(50, sneaker));
        Member member = memberRepository.save(validMember());

        SneakerStock sneakerStock275 = sneakerStockRepository.save(validStock(sneaker, 275, 30));
        sneakerItemRepository.save(validItem(sneakerStock275, draw, 275, 20));

        // when // then
        assertThatThrownBy(() ->
            drawTicketService.save(member.getId(), draw.getId(), 285)
        )
            .hasMessageContaining("입력 사이즈에 해당하는 신발이 존재하지 않습니다.")
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("memberID 로 DrawTicket을 조회한다.")
    void findDrawTicketsByMemberIdTest() {
        // given
        Sneaker sneaker1 = sneakerRepository.save(validSneaker());
        Sneaker sneaker2 = sneakerRepository.save(validSneaker());

        Draw draw1 = drawRepository.save(validDraw(50, sneaker1));
        Draw draw2 = drawRepository.save(validDraw(30, sneaker2));

        Member member = memberRepository.save(validMember());

        SneakerStock sneakerStock275 = sneakerStockRepository.save(validStock(sneaker1, 275, 30));
        sneakerItemRepository.save(validItem(sneakerStock275, draw1, 275, 30));

        SneakerStock sneakerStock285 = sneakerStockRepository.save(validStock(sneaker2, 285, 20));
        sneakerItemRepository.save(validItem(sneakerStock285, draw2, 285, 20));

        // when
        drawTicketService.save(member.getId(), draw1.getId(), 275);
        drawTicketService.save(member.getId(), draw2.getId(), 285);

        // then
        DrawTicketsResponse drawTicketResponses = drawTicketService.findByMemberId(member.getId());

        assertThat(drawTicketResponses.drawTicketResponses()).hasSize(2);
        assertAll(
            () -> assertThat(drawTicketResponses.drawTicketResponses().get(0).drawStatus())
                .isEqualTo(DrawStatus.WAITING),
            () -> assertThat(drawTicketResponses.drawTicketResponses().get(0).sneakerName())
                .isEqualTo("air jordan"),
            () -> assertThat(drawTicketResponses.drawTicketResponses().get(0).price())
                .isEqualTo(75000),
            () -> assertThat(drawTicketResponses.drawTicketResponses().get(0).code())
                .isEqualTo("AB1234"),
            () -> assertThat(drawTicketResponses.drawTicketResponses().get(0).size())
                .isEqualTo(275)
        );

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

    private Sneaker validSneaker() {
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

    private SneakerItem validItem(SneakerStock sneakerStock, Draw draw, int size, int quantity) {
        return SneakerItem.builder()
            .sneakerStock(sneakerStock)
            .draw(draw)
            .size(size)
            .quantity(quantity)
            .build();
    }

    private SneakerStock validStock(Sneaker sneaker, int size, int quantity) {
        SneakerStock stock = SneakerStock.builder()
            .size(size)
            .stock(new Stock(quantity))
            .build();
        stock.setSneaker(sneaker);

        return stock;
    }
}