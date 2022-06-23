package prgrms.neoike.controller.mapper;

import lombok.NoArgsConstructor;
import prgrms.neoike.controller.dto.sneaker.request.SneakerRegisterRequest;
import prgrms.neoike.controller.dto.sneaker.request.SneakerRequest;
import prgrms.neoike.controller.dto.sneaker.request.SneakerStockRequest;
import prgrms.neoike.service.dto.sneaker.SneakerDto;
import prgrms.neoike.service.dto.sneaker.SneakerRegisterDto;
import prgrms.neoike.service.dto.sneaker.SneakerStockDto;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class SneakerMapper {

    public static SneakerRegisterDto toDto(SneakerRegisterRequest registerRequest) {
        return new SneakerRegisterDto(
            registerRequest.imagePaths(),
            toDto(registerRequest.sneaker()),
            toDto(registerRequest.sneakerStocks())
        );
    }

    public static SneakerDto toDto(SneakerRequest sneakerRequest) {
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

    public static List<SneakerStockDto> toDto(List<SneakerStockRequest> sneakerStockRequests) {
        return sneakerStockRequests
            .stream()
            .map(i -> new SneakerStockDto(i.size(), i.quantity()))
            .toList();
    }
}
