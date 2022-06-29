package prgrms.neoike.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.domain.draw.DrawStatus;
import prgrms.neoike.domain.member.Address;
import prgrms.neoike.domain.member.CountryType;
import prgrms.neoike.domain.member.Email;
import prgrms.neoike.domain.member.Gender;
import prgrms.neoike.domain.member.Member;
import prgrms.neoike.domain.member.Password;
import prgrms.neoike.domain.member.PhoneNumber;
import prgrms.neoike.domain.sneaker.MemberCategory;
import prgrms.neoike.domain.sneaker.Sneaker;
import prgrms.neoike.domain.sneaker.SneakerCategory;
import prgrms.neoike.domain.sneaker.SneakerStock;
import prgrms.neoike.domain.sneaker.Stock;
import prgrms.neoike.repository.DrawTicketRepository;
import prgrms.neoike.repository.MemberRepository;
import prgrms.neoike.repository.SneakerRepository;
import prgrms.neoike.repository.SneakerStockRepository;
import prgrms.neoike.service.dto.drawdto.DrawResponse;
import prgrms.neoike.service.dto.drawdto.DrawSaveDto;
import prgrms.neoike.service.dto.drawdto.StockInfoDto;

@SpringBootTest
@Transactional
class DrawWinnnerServiceTest {

    @Autowired
    DrawService drawService;

    @Autowired
    DrawWinnnerService drawWinnnerService;

    @Autowired
    DrawTicketService drawTicketService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    DrawTicketRepository drawTicketRepository;

    @Autowired
    SneakerRepository sneakerRepository;

    @Autowired
    SneakerStockRepository sneakerStockRepository;

    LocalDateTime startDate = LocalDateTime.of(2025, 06, 12, 12, 00, 00);
    LocalDateTime endDate = LocalDateTime.of(2025, 06, 13, 12, 00, 00);
    LocalDateTime winningDate = LocalDateTime.of(2025, 06, 14, 12, 00, 00);

    @Test
    @DisplayName("추첨을 진행한다")
    void drawWinTest() {
        // given
        Sneaker sneaker = sneakerRepository.save(validSneaker());
        sneakerStockRepository.save(validSneakerStock(275, 5, sneaker));

        Map<Integer, Integer> sizeToQuantity = new HashMap<>() {{
            put(275, 5);
        }};

        DrawSaveDto drawSaveDto = validDrawSaveDto(sneaker.getId(), 5, sizeToQuantity);
        DrawResponse drawResponse = drawService.save(drawSaveDto);

        List<Member> members = Stream
            .generate(() -> memberRepository.save(validMember()))
            .limit(5)
            .toList();

        members.forEach(
            member -> drawTicketService.save(member.getId(), drawResponse.drawId(), 275)
        );

        // when
        drawWinnnerService.drawWinner(drawResponse.drawId());

        // then
        long count = members.stream()
            .map(member -> drawTicketRepository.findByMember(member).get(0).getDrawStatus())
            .filter(status -> status == DrawStatus.WINNING)
            .count();

        assertThat(count).isEqualTo(5);
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

    private SneakerStock validSneakerStock(int size, int quantity, Sneaker sneaker) {
        SneakerStock stock = SneakerStock.builder()
            .size(size)
            .stock(new Stock(quantity))
            .build();
        stock.setSneaker(sneaker);

        return stock;
    }

    private DrawSaveDto validDrawSaveDto(Long sneakerId, int quantity,
        Map<Integer, Integer> sizeToQuantity) {
        List<StockInfoDto> items = new ArrayList<>();
        sizeToQuantity.forEach(
            (size, quan) -> items.add(new StockInfoDto(size, quan))
        );

        return DrawSaveDto.builder()
            .sneakerId(sneakerId)
            .startDate(startDate)
            .endDate(endDate)
            .winningDate(winningDate)
            .sneakerStocks(items)
            .quantity(quantity)
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