package prgrms.neoike.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.common.exception.EntityNotFoundException;
import prgrms.neoike.service.dto.sneaker.*;

import javax.persistence.EntityExistsException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

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
        SneakerRegisterDto sneakerRegisterDto = registerServiceDto();
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
        SneakerRegisterDto sneakerRegisterDto = registerServiceDto();
        sneakerService.registerSneaker(sneakerRegisterDto);

        assertThatThrownBy(
            () -> sneakerService.registerSneaker(sneakerRegisterDto))
            .hasMessageContaining("동일한 신발이 이미 존재합니다.")
            .isInstanceOf(EntityExistsException.class);
    }

    @Test
    @DisplayName("신발 아이디와 코드로 신발을 상세조회 할 수 있다.")
    void testGetSneakerDetail() {
        SneakerIdResponse idResponse = sneakerService.registerSneaker(registerServiceDto());

        SneakerDetailDto detailDto = new SneakerDetailDto(idResponse.sneakerId(), idResponse.code());
        SneakerDetailResponse sneakerDetail = sneakerService.getSneakerDetail(detailDto);

        assertThat(sneakerDetail).isNotNull();

        SneakerResponse sneakerResponse = sneakerDetail.sneaker();
        List<SneakerStockResponse> sneakerStockResponses = sneakerDetail.sneakerStocks();

        assertAll(
            () -> assertThat(idResponse.code()).isEqualTo(sneakerResponse.code()),
            () -> assertThat(idResponse.sneakerId()).isEqualTo(sneakerResponse.sneakerId()),
            () -> assertThat(sneakerStockResponses).contains(
                new SneakerStockResponse(150, 10),
                new SneakerStockResponse(200, 10)
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
            .hasMessage(MessageFormat.format("요청한 신발 정보를 찾을 수 없습니다. (신발코드: {0})", detailDto.code()))
            .isInstanceOf(EntityNotFoundException.class);
    }

    static SneakerRegisterDto registerServiceDto() {
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