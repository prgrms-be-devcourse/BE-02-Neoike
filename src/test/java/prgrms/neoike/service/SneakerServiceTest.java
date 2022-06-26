package prgrms.neoike.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.service.dto.sneaker.SneakerDto;
import prgrms.neoike.service.dto.sneaker.SneakerRegisterDto;
import prgrms.neoike.service.dto.sneaker.SneakerResponse;
import prgrms.neoike.service.dto.sneaker.SneakerStockDto;

import javax.persistence.EntityExistsException;
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
        SneakerResponse sneakerResponse = sneakerService.registerSneaker(sneakerRegisterDto);

        assertThat(sneakerResponse).isNotNull();
        assertAll(
            () -> assertThat(sneakerResponse.sneakerId()).isNotNull().isNotNegative(),
            () -> assertThat(sneakerResponse.code()).isEqualTo(sneakerRegisterDto.sneakerDto().code())
        );
    }

    @Test
    @DisplayName("이미 같은 코드를 가진 신발이 등록되어 있다면 예외가 발생한다.")
    void testRegisterDuplicatedSneaker() {
        SneakerRegisterDto sneakerRegisterDto = registerServiceDto();
        SneakerResponse sneakerResponse = sneakerService.registerSneaker(sneakerRegisterDto);

        assertThatThrownBy(
            () -> { sneakerService.registerSneaker(sneakerRegisterDto); })
            .hasMessageContaining("동일한 신발이 이미 존재합니다.")
            .isInstanceOf(EntityExistsException.class);
    }

    static SneakerRegisterDto registerServiceDto() {
        return new SneakerRegisterDto(
            List.of("/src/main/~~~"),
            SneakerDto
                .builder()
                .memberCategory(MEN)
                .sneakerCategory(JORDAN)
                .name("test-sneaker")
                .price(100000)
                .code("DS-1000000")
                .description("this is test.")
                .releaseDate(LocalDateTime.now())
                .build(),
            List.of(
                new SneakerStockDto(150, 10),
                new SneakerStockDto(200, 10))
        );
    }
}