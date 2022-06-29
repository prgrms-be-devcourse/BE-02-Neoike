package prgrms.neoike.service.dto.page;

import lombok.Builder;

import java.util.List;

public record PageResponse<T>(
    List<T> contents,
    int page,
    int size,
    int totalPages,
    long totalElements,
    boolean sorted,
    boolean isFirst,
    boolean isLast
) {

    @Builder
    public PageResponse {
    }
}
