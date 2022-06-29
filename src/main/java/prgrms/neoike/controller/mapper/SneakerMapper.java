package prgrms.neoike.controller.mapper;

import lombok.NoArgsConstructor;
import prgrms.neoike.controller.dto.sneaker.SneakerRegisterRequest;
import prgrms.neoike.controller.dto.sneaker.SneakerRequest;
import prgrms.neoike.controller.dto.sneaker.SneakerStockRequest;
import prgrms.neoike.service.dto.sneaker.SneakerStockUpdateDto;
import prgrms.neoike.service.dto.page.PageableDto;
import prgrms.neoike.service.dto.sneaker.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class SneakerMapper {

    public static SneakerRegisterDto toSneakerRegisterDto(SneakerRegisterRequest registerRequest) {
        return new SneakerRegisterDto(
            registerRequest.imagePaths(),
            toSneakerDto(registerRequest.sneaker()),
            toSneakerStockDto(registerRequest.sneakerStocks())
        );
    }

    public static SneakerDto toSneakerDto(SneakerRequest sneakerRequest) {
        return SneakerDto
            .builder()
            .memberCategory(sneakerRequest.memberCategory())
            .sneakerCategory(sneakerRequest.sneakerCategory())
            .name(sneakerRequest.name())
            .price(sneakerRequest.price())
            .description(sneakerRequest.description())
            .code(sneakerRequest.code())
            .releaseDate(sneakerRequest.releaseDate())
            .build();
    }

    public static List<SneakerStockDto> toSneakerStockDto(List<SneakerStockRequest> sneakerStockRequests) {
        return sneakerStockRequests
            .stream()
            .map(i -> new SneakerStockDto(i.size(), i.quantity()))
            .toList();
    }

    public static SneakerDetailDto toSneakerDetailDto(Long sneakerId, String code) {
        return new SneakerDetailDto(sneakerId, code);
    }

    public static PageableDto toPagingDto(String page, String size, String sortBy, String direction) {
        return new PageableDto(page, size, sortBy, direction);
    }

    public static SneakerStockUpdateDto toSneakerStockUpdateDto(Long stockId, SneakerStockRequest stockRequest) {
        return new SneakerStockUpdateDto(stockId, stockRequest.size(), stockRequest.quantity());
    }
}
