package prgrms.neoike.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.domain.sneaker.Sneaker;
import prgrms.neoike.repository.SneakerRepository;
import prgrms.neoike.repository.SneakerStockRepository;
import prgrms.neoike.service.dto.sneaker.*;
import prgrms.neoike.service.image.ImageFileManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static prgrms.neoike.domain.sneaker.MemberCategory.MEN;
import static prgrms.neoike.domain.sneaker.SneakerCategory.JORDAN;

@Transactional
@ExtendWith(MockitoExtension.class)
class SneakerServiceTest {

    @InjectMocks
    SneakerService sneakerService;

    @Mock
    SneakerRepository sneakerRepository;

    @Mock
    SneakerStockRepository sneakerStockRepository;

    @Mock
    ImageFileManager imageFileManager;


    @Test
    @DisplayName("새로운 신발을 정상적으로 등록한다.")
    void testRegisterNewSneaker() {
        Sneaker sneaker = createSneaker();

        given(sneakerRepository.save(any())).willReturn(sneaker);

        SneakerResponse response = sneakerService.registerSneaker(registerServiceDto());

        assertThat(sneaker.getCode()).isEqualTo(response.code());
        assertThat(sneaker.getName()).isEqualTo(response.name());
    }

    static SneakerRegisterDto registerServiceDto() {
        return SneakerRegisterDto
            .builder()
            .sneakerDto(
                SneakerDto
                    .builder()
                    .memberCategory(MEN)
                    .sneakerCategory(JORDAN)
                    .name("test-sneaker")
                    .price(100000)
                    .code("DS-1000000")
                    .description("this is test.")
                    .releaseDate(LocalDateTime.now())
                    .build())
            .imageDtos(
                List.of(
                    new SneakerImageDto("test.PNG", "")))
            .stockDtos(
                List.of(
                    new SneakerStockDto(150, 10),
                    new SneakerStockDto(200, 10)))
            .build();
    }

    static Sneaker createSneaker() {
        return Sneaker
            .builder()
            .memberCategory(MEN)
            .sneakerCategory(JORDAN)
            .name("test-sneaker")
            .price(100000)
            .code("DS-1000000")
            .description("this is test.")
            .releaseDate(LocalDateTime.now())
            .build();
    }
}