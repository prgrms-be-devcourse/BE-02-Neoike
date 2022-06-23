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

    public static SneakerStock toEntity(SneakerStockDto stockDto) {
        return new SneakerStock(stockDto.size(), new Stock(stockDto.quantity()));
    }

    public static Sneaker toEntity(SneakerDto sneakerDto) {
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

    public static List<SneakerImage> toEntity(List<? extends String> paths) {
        return paths.stream().map(SneakerImage::new).toList();
    }

    public static SneakerResponse toResponse(Long id, String code) {
        return new SneakerResponse(id, code);
    }
}
