package prgrms.neoike.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.common.exception.EntityNotFoundException;
import prgrms.neoike.domain.draw.DrawStatus;
import prgrms.neoike.domain.member.*;
import prgrms.neoike.domain.sneaker.*;
import prgrms.neoike.repository.*;
import prgrms.neoike.service.dto.drawdto.DrawDto;
import prgrms.neoike.service.dto.drawdto.DrawResponse;
import prgrms.neoike.service.dto.drawdto.ServiceDrawSaveDto;
import prgrms.neoike.service.dto.drawdto.ServiceItemDto;

import static java.text.MessageFormat.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@SpringBootTest
@Transactional
class DrawServiceTest {
    @Autowired
    DrawService drawService;

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

    @Autowired
    SneakerItemRepository sneakerItemRepository;

    @Autowired
    DrawRepository drawRepository;

    @Autowired
    CacheManager cacheManager;

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
    }

    @Test
    @DisplayName("Draw 엔티티를 저장한다.")
    void saveDrawTest() {
        // given
        Sneaker sneaker = sneakerRepository.save(validSneaker());
        SneakerStock stock1 = sneakerStockRepository.save(validSneakerStock(275, 10, sneaker));
        SneakerStock stock2 = sneakerStockRepository.save(validSneakerStock(285, 10, sneaker));

        Map<Integer, Integer> sizeToQuantity = new HashMap<>(){{
            put(275, 3);
            put(285, 5);
        }};

        ServiceDrawSaveDto drawSaveDto = validDrawSaveDto(sneaker.getId(), 50, sizeToQuantity);

        // when
        DrawResponse drawResponse = drawService.save(drawSaveDto);

        // then
        assertThat(drawResponse.drawId()).isNotNull().isNotNegative();
        assertThat(stock1.getStock().getQuantity()).isEqualTo(7);
        assertThat(stock2.getStock().getQuantity()).isEqualTo(5);
    }
    @Test
    @DisplayName("SneakerStock 보다 많은 재고를 응모 item 으로 가지고 올때 응모 생성에 실패한다")
    void failSaveDrawTest1() {
        // given
        Sneaker sneaker = sneakerRepository.save(validSneaker());
        sneakerStockRepository.save(validSneakerStock(275, 10, sneaker));

        Map<Integer, Integer> sizeToQuantity = new HashMap<>(){{
            put(275, 50);
        }};

        ServiceDrawSaveDto drawSaveDto = validDrawSaveDto(sneaker.getId(), 50, sizeToQuantity);

        // when // then
        assertThatThrownBy(() -> drawService.save(drawSaveDto))
                .hasMessageContaining(format("재고가 부족합니다. (현재재고: {0})" ,10))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("저장된 stock 의 사이즈와 입력 사이즈가 달라 Draw 엔티티를 저장할때 오류 발생")
    void failSaveDrawTest() {
        // given
        Sneaker sneaker = sneakerRepository.save(validSneaker());
        sneakerStockRepository.save(validSneakerStock(275, 100, sneaker));
        sneakerStockRepository.save(validSneakerStock(285, 100, sneaker));

        Map<Integer, Integer> sizeToQuantity = new HashMap<>(){{
            put(275, 50);
            put(295, 5);
        }};

        ServiceDrawSaveDto drawSaveDto = validDrawSaveDto(sneaker.getId(), 50, sizeToQuantity);

        // when // then
        assertThatThrownBy(() -> drawService.save(drawSaveDto))
                .hasMessageContaining(format("SneakerStock 엔티티를 sneaker 와 size 로 찾을 수 없습니다. sneakerId : {0}, size : {1}", sneaker.getId(), 295))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("등록 순서가 잘못된 Draw 엔티티를 저장할때 오류 발생")
    void invalidSaveDrawTest() {
        // given
        Sneaker sneaker = sneakerRepository.save(validSneaker());

        // when // then
        assertThatThrownBy(() -> drawService.save(
                ServiceDrawSaveDto.builder()
                        .sneakerId(sneaker.getId())
                        .startDate(endDate)
                        .endDate(startDate)
                        .winningDate(winningDate)
                        .quantity(50)
                        .build()
        ))
                .hasMessageContaining(format("입력된 날짜의 순서가 맞지 않습니다. (startDate : {0} , endDate : {1})", endDate, startDate))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> drawService.save(
                ServiceDrawSaveDto.builder()
                        .sneakerId(sneaker.getId())
                        .startDate(startDate)
                        .endDate(winningDate)
                        .winningDate(endDate)
                        .quantity(50)
                        .build()
        ))
                .hasMessageContaining(format("입력된 날짜의 순서가 맞지 않습니다. (endDate : {0} , winningDate : {1})", winningDate, endDate))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    @DisplayName("추첨을 진행한다")
    void drawWinTest() {
        // given
        Sneaker sneaker = sneakerRepository.save(validSneaker());
        sneakerStockRepository.save(validSneakerStock(275, 5, sneaker));

        Map<Integer, Integer> sizeToQuantity = new HashMap<>(){{
            put(275, 5);
        }};

        ServiceDrawSaveDto drawSaveDto = validDrawSaveDto(sneaker.getId(), 5, sizeToQuantity);
        DrawResponse drawResponse = drawService.save(drawSaveDto);

        List<Member> members = Stream.generate(() -> memberRepository.save(validMember()))
                .limit(5)
                .toList();

        members.stream().forEach(
                (member) -> drawTicketService.save(member.getId(), drawResponse.drawId(), 275)
        );

        // when
        drawService.drawWinner(drawResponse.drawId());

        // then
        System.out.println(drawTicketRepository.findByMember(members.get(0)).get(0).getDrawStatus());
        long count = members.stream().map(
                        (member) -> drawTicketRepository.findByMember(member).get(0).getDrawStatus()
                )
                .filter((status) -> status == DrawStatus.WINNING)
                .count();

        assertThat(count).isEqualTo(5);
    }

    @Test
    @DisplayName("응모 리스트 조회 시 캐시 처리된다.")
    void testCacheGetAvailableDraws(){
        // given
        drawService.getAvailableDraws();

        // when
        List<DrawDto> drawDtos = cacheManager.getCache("draws")
            .get(SimpleKey.EMPTY, List.class);

        // then
        assertThat(drawDtos).isNotNull();
    }

    @Test
    @DisplayName("응모 생성 시 캐시가 삭제된다.")
    void testCacheEvictWhenCreateDraw(){
        // given
        drawService.getAvailableDraws();

        Sneaker sneaker = sneakerRepository.save(validSneaker());
        sneakerStockRepository.save(validSneakerStock(275, 10, sneaker));
        sneakerStockRepository.save(validSneakerStock(285, 10, sneaker));

        Map<Integer, Integer> sizeToQuantity = new HashMap<>(){{
            put(275, 3);
            put(285, 5);
        }};

        ServiceDrawSaveDto drawSaveDto = validDrawSaveDto(sneaker.getId(), 50, sizeToQuantity);

        // when
        drawService.save(drawSaveDto);

        // then
        ValueWrapper evicted = cacheManager.getCache("draws").get(SimpleKey.EMPTY);
        assertThat(evicted).isNull();
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

    private ServiceDrawSaveDto validDrawSaveDto(Long sneakerId, int quantity, Map<Integer, Integer> sizeToQuantity) {
        List<ServiceItemDto> items = new ArrayList<>();
        sizeToQuantity.forEach (
                (size, quan) -> items.add(new ServiceItemDto(size, quan))
        );

        return ServiceDrawSaveDto.builder()
                .sneakerId(sneakerId)
                .startDate(startDate)
                .endDate(endDate)
                .winningDate(winningDate)
                .sneakerItems(items)
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