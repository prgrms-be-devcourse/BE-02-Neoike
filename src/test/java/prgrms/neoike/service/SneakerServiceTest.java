package prgrms.neoike.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.QueryParameterException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.common.exception.EntityNotFoundException;
import prgrms.neoike.service.dto.page.PageResponse;
import prgrms.neoike.service.dto.page.PageableDto;
import prgrms.neoike.service.dto.sneaker.*;

import javax.persistence.EntityExistsException;
import java.time.LocalDateTime;
import java.util.List;

import static java.text.MessageFormat.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static prgrms.neoike.domain.sneaker.MemberCategory.MEN;
import static prgrms.neoike.domain.sneaker.SneakerCategory.JORDAN;

@Transactional
@SpringBootTest
class SneakerServiceTest {

    @Autowired
    private SneakerService sneakerService;

    @Test
    @DisplayName("새로운 신발을 정상적으로 등록한다.")
    void testRegisterSneaker() {
        SneakerRegisterDto sneakerRegisterDto = createRegisterDto();
        SneakerIdResponse sneakerIdResponse = sneakerService.registerSneaker(sneakerRegisterDto);

        assertThat(sneakerIdResponse).isNotNull();
        assertAll(
            () -> assertThat(sneakerIdResponse.sneakerId()).isNotNull().isNotNegative(),
            () -> assertThat(sneakerIdResponse.code()).isEqualTo(sneakerRegisterDto.sneakerDto().code())
        );
    }

    @Test
    @DisplayName("이미 같은 코드를 가진 신발이 등록되어 있다면 예외가 발생한다.")
    void testRegisterDuplicatedSneaker() {
        SneakerRegisterDto sneakerRegisterDto = createRegisterDto();
        sneakerService.registerSneaker(sneakerRegisterDto);

        assertThatThrownBy(
            () -> sneakerService.registerSneaker(sneakerRegisterDto))
            .hasMessageContaining("동일한 신발이 이미 존재합니다.")
            .isInstanceOf(EntityExistsException.class);
    }

    @Test
    @DisplayName("신발 아이디와 코드로 신발을 상세조회 할 수 있다.")
    void testGetSneakerDetail() {
        SneakerIdResponse idResponse = sneakerService.registerSneaker(createRegisterDto());

        SneakerDetailDto detailDto = new SneakerDetailDto(idResponse.sneakerId(), idResponse.code());
        SneakerDetailResponse sneakerDetail = sneakerService.getSneakerDetail(detailDto);

        assertThat(sneakerDetail).isNotNull();

        SneakerResponse sneakerResponse = sneakerDetail.sneaker();
        List<SneakerStockResponse> sneakerStockResponses = sneakerDetail.sneakerStocks();

        assertAll(
            () -> assertThat(idResponse.code()).isEqualTo(sneakerResponse.code()),
            () -> assertThat(idResponse.sneakerId()).isEqualTo(sneakerResponse.sneakerId()),
            () -> assertThat(sneakerStockResponses).contains(
                new SneakerStockResponse(sneakerStockResponses.get(0).stockId(), 150, 10, sneakerResponse.sneakerId()),
                new SneakerStockResponse(sneakerStockResponses.get(1).stockId(), 200, 10, sneakerResponse.sneakerId())
            )
        );
    }

    @RepeatedTest(value = 10, name = "존재하지 않는 신발 조회 {currentRepetition} of {totalRepetitions}")
    @DisplayName("등록되지 않은 상품을 조회하면 예외가 발생한다.")
    void testNotFoundEntityException() {
        String sneakerCode = RandomStringUtils.randomAlphanumeric(2) + "-" + RandomStringUtils.randomNumeric(7);
        SneakerDetailDto detailDto = new SneakerDetailDto(Long.parseLong(RandomStringUtils.randomNumeric(10)), sneakerCode);

        assertThatThrownBy(
            () -> sneakerService.getSneakerDetail(detailDto))
            .hasMessage(format("요청한 신발 정보를 찾을 수 없습니다. (신발코드: {0})", detailDto.code()))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("신발을 페이징 처리를 하여 전체조회 할 수 있다.")
    void testGetSneakers() {
        register5Sneakers();
        PageResponse<SneakerResponse> pageResponse = sneakerService.getSneakers(new PageableDto(1, 5, "id.desc"));

        assertThat(pageResponse).isNotNull();
        assertAll(
            () -> assertThat(pageResponse.page()).isEqualTo(1),
            () -> assertThat(pageResponse.isFirst()).isTrue(),
            () -> assertThat(pageResponse.sorted()).isTrue(),
            () -> assertThat(pageResponse.totalElements()).isEqualTo(5)
        );

        List<SneakerResponse> contents = pageResponse.contents();

        assertThat(contents).isNotEmpty();
        assertAll(
            () -> assertThat(contents).hasSize(5),
            () -> assertThat(contents.get(0)).isInstanceOf(SneakerResponse.class)
        );
    }

    @Test
    @DisplayName("유효하지 않은 페이징 정렬 정보가 입력되면 예외가 발생한다.")
    void testInvalidPagingParameter() {
        register5Sneakers();

        assertThatThrownBy(
            () -> sneakerService.getSneakers(new PageableDto(1, 5, "id.ABCDE")))
            .hasMessage(format("페이징 정렬에 실패하였습니다. (direction: {0})", "ABCDE"))
            .isInstanceOf(QueryParameterException.class);
    }

    @Test
    @Rollback
    @DisplayName("입력된 신발 재고의 아이디와 사이즈에 맞는 재고를 찾아서 입력된 수량만큼 감소시킨다.")
    void testDecreaseSneakerStock() {
        SneakerStockResponse stockResponse = getStockResponse();
        int countOfBeforeDecrease = stockResponse.quantity();
        SneakerStockUpdateDto updateDto = new SneakerStockUpdateDto(stockResponse.stockId(), stockResponse.size(), 1);
        SneakerStockResponse decreasedStockResponse = sneakerService.decreaseSneakerStock(updateDto);

        assertAll(
            () -> assertThat(decreasedStockResponse.stockId()).isEqualTo(stockResponse.stockId()),
            () -> assertThat(decreasedStockResponse.size()).isEqualTo(stockResponse.size()),
            () -> assertThat(decreasedStockResponse.quantity()).isLessThan(stockResponse.quantity()),
            () -> assertThat(countOfBeforeDecrease - updateDto.quantity()).isEqualTo(decreasedStockResponse.quantity())
        );
    }

    @Test
    @Rollback
    @DisplayName("입력된 신발 재고의 아이디와 사이즈에 맞는 재고를 찾아서 입력된 수량만큼 증가시킨다.")
    void testIncreaseSneakerStock() {
        SneakerStockResponse stockResponse = getStockResponse();
        int countOfBeforeIncrease = stockResponse.quantity();
        SneakerStockUpdateDto updateDto = new SneakerStockUpdateDto(stockResponse.stockId(), stockResponse.size(), 1);
        SneakerStockResponse increasedStockResponse = sneakerService.increaseSneakerStock(updateDto);

        assertAll(
            () -> assertThat(increasedStockResponse.stockId()).isEqualTo(stockResponse.stockId()),
            () -> assertThat(increasedStockResponse.size()).isEqualTo(stockResponse.size()),
            () -> assertThat(increasedStockResponse.quantity()).isGreaterThan(stockResponse.quantity()),
            () -> assertThat(countOfBeforeIncrease + updateDto.quantity()).isEqualTo(increasedStockResponse.quantity())
        );
    }

    @Test
    @DisplayName("현재 신발의 재고 수량보다 더 큰 수량이 입력되면 예외가 발생한다.")
    void testNotEnoughStockQuantity() {
        SneakerStockResponse stockResponse = getStockResponse();
        SneakerStockUpdateDto updateDto = new SneakerStockUpdateDto(stockResponse.stockId(), stockResponse.size(), 999999999);

        assertThatThrownBy(
            () -> sneakerService.decreaseSneakerStock(updateDto))
            .hasMessage(format("재고가 부족합니다. (현재재고: {0})", stockResponse.quantity()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private SneakerStockResponse getStockResponse() {
        SneakerRegisterDto registerDto = createRegisterDto();
        SneakerIdResponse sneakerIdResponse = sneakerService.registerSneaker(registerDto);
        SneakerDetailDto detailDto = new SneakerDetailDto(sneakerIdResponse.sneakerId(), sneakerIdResponse.code());
        SneakerDetailResponse sneakerDetail = sneakerService.getSneakerDetail(detailDto);
        List<SneakerStockResponse> sneakerStockResponses = sneakerDetail.sneakerStocks();

        return sneakerStockResponses.get(0);
    }

    private void register5Sneakers() {
        for (int i = 0; i < 5; i++) {
            sneakerService.registerSneaker(createRegisterDto());
        }
    }

    private static SneakerRegisterDto createRegisterDto() {
        return new SneakerRegisterDto(
            List.of("/src/main/~~~"),
            SneakerDto
                .builder()
                .memberCategory(MEN)
                .sneakerCategory(JORDAN)
                .name("sneaker-" + RandomStringUtils.random(5))
                .price(100000)
                .code("DS-" + RandomStringUtils.randomNumeric(7))
                .description("this is test.")
                .releaseDate(LocalDateTime.now())
                .build(),
            List.of(
                new SneakerStockDto(150, 10),
                new SneakerStockDto(200, 10))
        );
    }
}