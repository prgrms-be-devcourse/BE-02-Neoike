package prgrms.neoike.service.converter;

import lombok.NoArgsConstructor;
import org.hibernate.QueryParameterException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import prgrms.neoike.domain.sneaker.Sneaker;
import prgrms.neoike.service.dto.page.PageResponse;
import prgrms.neoike.service.dto.page.PageableDto;
import prgrms.neoike.service.dto.sneaker.SneakerResponse;

import java.util.Collections;
import java.util.List;

import static java.text.MessageFormat.format;
import static lombok.AccessLevel.PRIVATE;
import static prgrms.neoike.service.converter.SneakerConverter.toImagePaths;

@NoArgsConstructor(access = PRIVATE)
public class PageConverter {

    private static final int DEFAULT_SIZE = 20;

    public static Pageable toPageable(PageableDto pageableDto) {
        int page = validateRequestPage(pageableDto.page());
        int size = validateSize(pageableDto.size());
        String sortBy = pageableDto.sortBy();

        int dotIndex = sortBy.lastIndexOf(".");
        String property = sortBy.substring(0, dotIndex);
        Direction direction = toDirection(sortBy.substring(dotIndex + 1));

        return PageRequest.of(page, size, direction, property);
    }

    public static PageResponse<SneakerResponse> toSneakerResponses(Page<Sneaker> sneakers) {
        return PageResponse.<SneakerResponse>builder()
            .contents(toSneakerResponse(sneakers.getContent()))
            .page(sneakers.getNumber() + 1)
            .size(sneakers.getSize())
            .sorted(sneakers.getSort().isSorted())
            .totalPages(sneakers.getTotalPages())
            .totalElements(sneakers.getTotalElements())
            .isFirst(sneakers.isFirst())
            .isLast(sneakers.isLast())
            .build();
    }

    public static List<SneakerResponse> toSneakerResponse(List<Sneaker> sneakers) {
        if (sneakers.isEmpty()) {
            return Collections.emptyList();
        }

        return sneakers
            .stream()
            .map(
                i -> SneakerResponse.builder()
                    .sneakerId(i.getId())
                    .memberCategory(i.getMemberCategory())
                    .sneakerCategory(i.getSneakerCategory())
                    .name(i.getName())
                    .price(i.getPrice())
                    .description(i.getDescription())
                    .releaseDate(i.getReleaseDate())
                    .code(i.getCode())
                    .imagePaths(toImagePaths(i.getSneakerImages()))
                    .build()
            ).toList();
    }

    private static int validateRequestPage(int page) {
        return page < 1 ? 0 : page - 1;
    }

    private static int validateSize(int size) {
        if (size > 100 || size < 0) {
            return DEFAULT_SIZE;
        }

        return size;
    }

    private static Direction toDirection(String direction) {
        return Direction
            .fromOptionalString(direction.toUpperCase())
            .orElseThrow(
                () -> new QueryParameterException(
                    format("페이징 정렬에 실패하였습니다. (direction: {0})", direction)
                )
            );
    }
}
