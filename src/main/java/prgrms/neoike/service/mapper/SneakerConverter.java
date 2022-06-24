package prgrms.neoike.service.mapper;

import lombok.NoArgsConstructor;
import prgrms.neoike.domain.sneaker.Sneaker;
import prgrms.neoike.domain.sneaker.SneakerImage;
import prgrms.neoike.domain.sneaker.SneakerStock;
import prgrms.neoike.domain.sneaker.Stock;
import prgrms.neoike.service.dto.sneaker.SneakerDto;
import prgrms.neoike.service.dto.sneaker.SneakerResponse;
import prgrms.neoike.service.dto.sneaker.SneakerStockDto;

import java.util.List;

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

    public static SneakerResponse toSneakerResponse(Long id, String code) {
        return new SneakerResponse(id, code);
    }
}
