package prgrms.neoike.controller.mapper;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import prgrms.neoike.controller.dto.drawdto.DrawSaveRequest;
import prgrms.neoike.controller.dto.drawdto.StockInfo;
import prgrms.neoike.service.dto.drawdto.DrawSaveDto;
import prgrms.neoike.service.dto.drawdto.StockInfoDto;

@NoArgsConstructor(access = PRIVATE)
public class DrawMapper {

    public static DrawSaveDto toDrawSaveDto(DrawSaveRequest drawSaveRequest) {
        return new DrawSaveDto(
            drawSaveRequest.sneakerId(),
            drawSaveRequest.startDate(),
            drawSaveRequest.endDate(),
            drawSaveRequest.winningDate(),
            drawSaveRequest.sneakerStocks().stream()
                .map(DrawMapper::toItemDto)
                .toList(),
            drawSaveRequest.quantity()
        );
    }

    private static StockInfoDto toItemDto(StockInfo stockInfo) {
        return new StockInfoDto(
            stockInfo.size(),
            stockInfo.quantity()
        );
    }
}
