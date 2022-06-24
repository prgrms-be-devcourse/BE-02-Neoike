package prgrms.neoike.controller.mapper;

import lombok.NoArgsConstructor;
import prgrms.neoike.controller.dto.sneaker.SneakerRegisterRequest;
import prgrms.neoike.controller.dto.sneaker.SneakerRequest;
import prgrms.neoike.controller.dto.sneaker.SneakerStockRequest;
import prgrms.neoike.service.dto.sneaker.SneakerDetailDto;
import prgrms.neoike.service.dto.sneaker.SneakerDto;
import prgrms.neoike.service.dto.sneaker.SneakerRegisterDto;
import prgrms.neoike.service.dto.sneaker.SneakerStockDto;

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
}
