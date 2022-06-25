package prgrms.neoike.service.converter;

import lombok.NoArgsConstructor;
import prgrms.neoike.domain.sneaker.Sneaker;
import prgrms.neoike.domain.sneaker.SneakerImage;
import prgrms.neoike.domain.sneaker.SneakerStock;
import prgrms.neoike.domain.sneaker.Stock;
import prgrms.neoike.service.dto.sneaker.*;

import java.util.List;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class SneakerConverter {

    public static List<SneakerStock> toSneakerStockEntities(List<SneakerStockDto> stockDto) {
        return stockDto
            .stream()
            .map(s -> new SneakerStock(s.size(), new Stock(s.quantity())))
            .toList();
    }

    public static Sneaker toSneakerEntity(SneakerDto sneakerDto) {
        return Sneaker.
            builder()
            .memberCategory(sneakerDto.memberCategory())
            .sneakerCategory(sneakerDto.sneakerCategory())
            .name(sneakerDto.name())
            .price(sneakerDto.price())
            .description(sneakerDto.description())
            .code(sneakerDto.code())
            .releaseDate(sneakerDto.releaseDate())
            .build();
    }

    public static List<SneakerImage> toSneakerImageEntity(List<String> paths) {
        return paths.stream().map(SneakerImage::new).toList();
    }

    public static SneakerIdResponse toSneakerIdResponse(Long id, String code) {
        return new SneakerIdResponse(id, code);
    }

    public static SneakerDetailResponse toSneakerDetailResponse(List<SneakerStock> sneakerStocks) {
        Sneaker sneaker = sneakerStocks.get(0).getSneaker();

        return new SneakerDetailResponse(
            toSneakerStockResponse(sneakerStocks),
            toSneakerResponse(sneaker)
        );
    }

    private static List<SneakerStockResponse> toSneakerStockResponse(List<SneakerStock> sneakerStocks) {
        return sneakerStocks
            .stream()
            .map(stock -> new SneakerStockResponse(stock.getSize(), stock.getStock().getQuantity()))
            .toList();
    }

    public static SneakerResponse toSneakerResponse(Sneaker sneaker) {
        return SneakerResponse
            .builder()
            .sneakerId(sneaker.getId())
            .memberCategory(sneaker.getMemberCategory())
            .sneakerCategory(sneaker.getSneakerCategory())
            .name(sneaker.getName())
            .price(sneaker.getPrice())
            .description(sneaker.getDescription())
            .code(sneaker.getCode())
            .releaseDate(sneaker.getReleaseDate())
            .imagePaths(toImagePaths(sneaker.getSneakerImages()))
            .build();
    }

    public static List<String> toImagePaths(Set<SneakerImage> images) {
        return images.stream().map(SneakerImage::getPath).toList();
    }
}
