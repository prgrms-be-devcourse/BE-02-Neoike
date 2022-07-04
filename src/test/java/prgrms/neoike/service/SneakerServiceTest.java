package prgrms.neoike.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import prgrms.neoike.common.exception.EntityNotFoundException;
import prgrms.neoike.domain.sneaker.Sneaker;
import prgrms.neoike.domain.sneaker.SneakerStock;
import prgrms.neoike.domain.sneaker.Stock;
import prgrms.neoike.repository.SneakerRepository;
import prgrms.neoike.repository.SneakerStockRepository;
import prgrms.neoike.service.dto.page.PageResponse;
import prgrms.neoike.service.dto.page.PageableDto;
import prgrms.neoike.service.dto.sneaker.*;

import javax.persistence.EntityExistsException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static prgrms.neoike.domain.sneaker.MemberCategory.MEN;
import static prgrms.neoike.domain.sneaker.SneakerCategory.JORDAN;

@ExtendWith(MockitoExtension.class)
class SneakerServiceTest {

    @InjectMocks
    private SneakerService sneakerService;

    @Mock
    private SneakerRepository sneakerRepository;

    @Mock
    private SneakerStockRepository sneakerStockRepository;

    @Test
    @DisplayName("새로운 신발을 정상적으로 등록한다.")
    void testRegisterSneaker() {
        //given
        Long sneakerId = 1L;
        SneakerRegisterDto registerDto = createRegisterDto();
        Sneaker sneaker = mock(Sneaker.class);

        given(sneakerRepository.save(any())).willReturn(sneaker);
        given(sneaker.getId()).willReturn(sneakerId);

        //when
        SneakerIdResponse sneakerIdResponse = sneakerService.registerSneaker(registerDto);

        //then
        assertThat(sneakerIdResponse.sneakerId()).isEqualTo(sneakerId);
    }

    @Test
    @DisplayName("이미 같은 코드를 가진 신발이 등록되어 있다면 예외가 발생한다.")
    void testRegisterDuplicatedSneaker() {
        //given
        SneakerRegisterDto registerDto = createRegisterDto();
        String code = registerDto.sneakerDto().code();
        Sneaker sneaker = mock(Sneaker.class);

        given(sneakerRepository.findByCode(code)).willReturn(Optional.of(sneaker));

        //when, then
        assertThatThrownBy(
            () -> sneakerService.registerSneaker(registerDto)
        ).isInstanceOf(EntityExistsException.class);
    }

    @Test
    @DisplayName("신발 아이디와 코드로 신발을 상세조회 할 수 있다.")
    void testGetSneakerDetail() {
        //given
        SneakerDetailDto detailDto = new SneakerDetailDto(1L, "DS-1234567");
        SneakerStock sneakerStock1 = new SneakerStock(250, new Stock(100));
        SneakerStock sneakerStock2 = new SneakerStock(260, new Stock(120));
        Sneaker sneaker = mock(Sneaker.class);
        sneakerStock1.setSneaker(sneaker);
        sneakerStock2.setSneaker(sneaker);

        given(sneakerStockRepository.findByIdAndCode(any(), any()))
            .willReturn(List.of(sneakerStock1, sneakerStock2));

        //when
        SneakerDetailResponse sneakerDetail = sneakerService.getSneakerDetail(detailDto);

        //then
        List<SneakerStockResponse> sneakerStockResponses = sneakerDetail.sneakerStocks();

        assertThat(sneakerDetail.sneaker().sneakerId()).isEqualTo(sneaker.getId());
        assertThat(sneakerStockResponses.get(0).size()).isEqualTo(sneakerStock1.getSize());
        assertThat(sneakerStockResponses.get(0).quantity()).isEqualTo(sneakerStock1.getStock().getQuantity());
        assertThat(sneakerStockResponses.get(1).size()).isEqualTo(sneakerStock2.getSize());
        assertThat(sneakerStockResponses.get(1).quantity()).isEqualTo(sneakerStock2.getStock().getQuantity());
    }

    @Test
    @DisplayName("등록되지 않은 상품을 조회하면 예외가 발생한다.")
    void testNotFoundEntityException() {
        //given
        String unknownCode = "DS-9999999";
        long unknownId = 10L;

        given(sneakerStockRepository.findByIdAndCode(unknownId, unknownCode)).willReturn(emptyList());

        //when, then
        assertThatThrownBy(
            () -> sneakerService.getSneakerDetail(new SneakerDetailDto(unknownId, unknownCode))
        ).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("신발을 페이징 처리를 하여 전체조회 할 수 있다.")
    void testGetSneakers() {
        //given
        int page = 1;
        int size = 2;
        String sortBy = "createdAt";
        String direction = "desc";

        PageableDto pageableDto = new PageableDto(String.valueOf(page), String.valueOf(size), sortBy, direction);
        PageRequest pageable = PageRequest
            .of(0, size, Sort.Direction.fromString(direction), sortBy);
        List<Sneaker> sneakers = List.of(mock(Sneaker.class), mock(Sneaker.class), mock(Sneaker.class));
        Page<Sneaker> sneakerPage = new PageImpl<>(sneakers, pageable, sneakers.size());

        given(sneakerRepository.findAll(pageable)).willReturn(sneakerPage);

        //when
        PageResponse<SneakerResponse> pageResponse = sneakerService.getSneakers(pageableDto);

        //then
        assertThat(pageResponse.page()).isEqualTo(page);
        assertThat(pageResponse.size()).isEqualTo(size);
        assertThat(pageResponse.sorted()).isTrue();
        assertThat(pageResponse.totalElements()).isEqualTo(sneakers.size());
    }

    @Test
    @DisplayName("입력된 신발 재고의 아이디와 사이즈에 맞는 재고를 찾아서 입력된 수량이 음수면 재고를 감소시킨다.")
    void testSneakerStockDecrement() {
        //given
        int originQuantity = 10;
        int decreaseQuantity = -5;

        SneakerStockUpdateDto updateDto = new SneakerStockUpdateDto(2L, 250, decreaseQuantity);
        SneakerStock sneakerStock = new SneakerStock(250, new Stock(10));
        Sneaker sneaker = mock(Sneaker.class);
        sneakerStock.setSneaker(sneaker);

        given(sneakerStockRepository.findByIdAndSize(anyLong(), anyInt()))
            .willReturn(Optional.of(sneakerStock));

        //when
        SneakerStockResponse decreasedSneakerStock = sneakerService.manageSneakerStock(updateDto);

        //then
        assertThat(decreasedSneakerStock.quantity()).isEqualTo(originQuantity - Math.abs(decreaseQuantity));
    }

    @Test
    @DisplayName("입력된 신발 재고의 아이디와 사이즈에 맞는 재고를 찾아서 입력된 수량만큼 증가시킨다.")
    void testSneakerStockIncrement() {
        //given
        int originQuantity = 10;
        int increaseQuantity = 5;

        SneakerStockUpdateDto updateDto = new SneakerStockUpdateDto(2L, 250, increaseQuantity);
        SneakerStock sneakerStock = new SneakerStock(250, new Stock(10));
        Sneaker sneaker = mock(Sneaker.class);
        sneakerStock.setSneaker(sneaker);

        given(sneakerStockRepository.findByIdAndSize(anyLong(), anyInt()))
            .willReturn(Optional.of(sneakerStock));

        //when
        SneakerStockResponse decreasedSneakerStock = sneakerService.manageSneakerStock(updateDto);

        //then
        assertThat(decreasedSneakerStock.quantity()).isEqualTo(originQuantity + increaseQuantity);
    }

    @Test
    @DisplayName("현재 신발의 재고 수량보다 더 큰 수량이 입력되면 예외가 발생한다.")
    void testNotEnoughStockQuantity() {
        //given
        int originQuantity = 10;
        int decreaseQuantity = -100;

        SneakerStockUpdateDto updateDto = new SneakerStockUpdateDto(2L, 250, decreaseQuantity);
        SneakerStock sneakerStock = new SneakerStock(250, new Stock(10));
        Sneaker sneaker = mock(Sneaker.class);
        sneakerStock.setSneaker(sneaker);

        given(sneakerStockRepository.findByIdAndSize(anyLong(), anyInt()))
            .willReturn(Optional.of(sneakerStock));

        //when, then
        assertThatThrownBy(
            () -> sneakerService.manageSneakerStock(updateDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(MessageFormat.format("재고가 부족합니다. (현재재고: {0})", originQuantity));
    }


    private SneakerRegisterDto createRegisterDto() {
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