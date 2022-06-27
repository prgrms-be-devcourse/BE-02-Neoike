package prgrms.neoike.service.converter;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import prgrms.neoike.domain.sneaker.Sneaker;
import prgrms.neoike.service.dto.page.PageResponse;
import prgrms.neoike.service.dto.page.PageableDto;
import prgrms.neoike.service.dto.sneaker.SneakerResponse;

import java.util.Set;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static prgrms.neoike.service.converter.SneakerConverter.toSneakerContents;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class PageConverter {

    private static final int DEFAULT_SIZE = 20;
    private static final int DEFAULT_PAGE = 0;
    private static final String DEFAULT_SORT_BY = "createdAt";
    private static final Direction DEFAULT_DIRECTION = DESC;
    private static final Set<String> SNEAKER_SORT_BY = Set.of("id", "releaseDate", "price");

    public static Pageable toPageable(PageableDto pageableDto) {
        int page = toPage(pageableDto.page());
        int size = toSize(pageableDto.size());
        String sortBy = toSortBy(pageableDto.sortBy());
        Direction direction = toDirection(pageableDto.direction());

        return PageRequest.of(page, size, direction, sortBy);
    }

    public static PageResponse<SneakerResponse> toSneakerResponses(Page<Sneaker> sneakers) {
        return PageResponse.<SneakerResponse>builder()
            .contents(toSneakerContents(sneakers.getContent()))
            .page(sneakers.getNumber() + 1)
            .size(sneakers.getSize())
            .sorted(sneakers.getSort().isSorted())
            .totalPages(sneakers.getTotalPages())
            .totalElements(sneakers.getTotalElements())
            .isFirst(sneakers.isFirst())
            .isLast(sneakers.isLast())
            .build();
    }

    private static int toPage(String page) {
        int pageRequest;

        try {
            pageRequest = Integer.parseInt(page);
        } catch (NumberFormatException ex) {
            log.warn("잘못된 페이지 요청이 들어왔습니다. : {}", ex.getMessage(), ex);

            return DEFAULT_PAGE;
        }

        return pageRequest < 1 ? 0 : pageRequest - 1;
    }

    private static int toSize(String size) {
        int sizeRequest;

        try {
            sizeRequest = Integer.parseInt(size);
        } catch (NumberFormatException ex) {
            log.warn("잘못된 페이지 사이즈 요청이 들어왔습니다. : {}", ex.getMessage(), ex);

            return DEFAULT_SIZE;
        }

        return sizeRequest > 100 || sizeRequest < 0 ? DEFAULT_SIZE : sizeRequest;
    }

    private static String toSortBy(String sortBy) {
        return SNEAKER_SORT_BY.contains(sortBy) ? sortBy : DEFAULT_SORT_BY;
    }

    private static Direction toDirection(String direction) {
        return Direction.fromOptionalString(direction).orElse(DEFAULT_DIRECTION);
    }
}
