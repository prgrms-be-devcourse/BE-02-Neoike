package prgrms.neoike.controller.mapper;

import lombok.NoArgsConstructor;
import prgrms.neoike.controller.dto.sneaker.request.SneakerImageRequest;
import prgrms.neoike.controller.dto.sneaker.request.SneakerRegisterRequest;
import prgrms.neoike.controller.dto.sneaker.request.SneakerRequest;
import prgrms.neoike.controller.dto.sneaker.request.SneakerStockRequest;
import prgrms.neoike.service.dto.sneaker.SneakerDto;
import prgrms.neoike.service.dto.sneaker.SneakerImageDto;
import prgrms.neoike.service.dto.sneaker.SneakerRegisterDto;
import prgrms.neoike.service.dto.sneaker.SneakerStockDto;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class SneakerMapper {

    public static SneakerRegisterDto toDto(SneakerRegisterRequest registerRequest) {
        return SneakerRegisterDto
            .builder()
            .imageDtos(registerRequest.sneakerImages().stream().map(SneakerMapper::toDto).toList())
            .sneakerDto(toDto(registerRequest.sneaker()))
            .stockDtos(registerRequest.sneakerStocks().stream().map(SneakerMapper::toDto).toList())
            .build();
    }

    public static SneakerImageDto toDto(SneakerImageRequest imageRequest) {
        return new SneakerImageDto(imageRequest.originName(), imageRequest.encodedName());
    }

    public static SneakerDto toDto(SneakerRequest sneakerRequest) {
        return SneakerDto
            .builder()
            .memberCategory(sneakerRequest.memberCategory())
            .sneakerCategory(sneakerRequest.sneakerCategory())
            .name(sneakerRequest.name())
            .price(sneakerRequest.price())
            .description(sneakerRequest.description())
            .code(
                sneakerRequest.code().isBlank() ?
                    SneakerCodeCreator.createSneakerCode(sneakerRequest.sneakerCategory())
                    : sneakerRequest.code()
            )
            .releaseDate(sneakerRequest.releaseDate())
            .build();
    }

    public static SneakerStockDto toDto(SneakerStockRequest stockRequest) {
        return new SneakerStockDto(stockRequest.size(), stockRequest.quantity());
    }
}
