package prgrms.neoike.controller.mapper;

import org.springframework.stereotype.Component;
import prgrms.neoike.controller.dto.drawdto.DrawSaveRequest;
import prgrms.neoike.controller.dto.drawdto.StockInfo;
import prgrms.neoike.service.dto.drawdto.DrawSaveDto;
import prgrms.neoike.service.dto.drawdto.StockInfoDto;

@Component
public class DrawMapper {

    public DrawSaveDto toDrawSaveDto(DrawSaveRequest drawSaveRequest) {
        return new DrawSaveDto(
            drawSaveRequest.sneakerId(),
            drawSaveRequest.startDate(),
            drawSaveRequest.endDate(),
            drawSaveRequest.winningDate(),
            drawSaveRequest.sneakerStocks().stream()
                .map(this::toItemDto)
                .toList(),
            drawSaveRequest.quantity()
        );
    }

    private StockInfoDto toItemDto(StockInfo stockInfo) {
        return new StockInfoDto(
            stockInfo.size(),
            stockInfo.quantity()
        );
    }
}
