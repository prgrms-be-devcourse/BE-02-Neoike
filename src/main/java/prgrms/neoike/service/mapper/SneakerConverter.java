package prgrms.neoike.service.mapper;

import lombok.NoArgsConstructor;
import prgrms.neoike.domain.sneaker.*;
import prgrms.neoike.service.dto.sneaker.SneakerDto;
import prgrms.neoike.service.dto.sneaker.SneakerResponse;
import prgrms.neoike.service.dto.sneaker.SneakerStockDto;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class SneakerConverter {

    public static List<SneakerStock> toEntity(List<SneakerStockDto> stockDtos) {
        return stockDtos
            .stream()
            .map(dto -> new SneakerStock(dto.size(), new Stock(dto.quantity())))
            .toList();
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

    public static Set<SneakerImage> toEntity(Set<Image> images) {
        return images.stream().map(SneakerImage::new).collect(toSet());
    }

    public static SneakerResponse toResponse(Long id, String name, String code) {
        return new SneakerResponse(id, name, code);
    }
}
