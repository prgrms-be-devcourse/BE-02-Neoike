package prgrms.neoike.controller.mapper;

import org.springframework.stereotype.Component;
import prgrms.neoike.controller.dto.drawdto.DrawSaveRequest;
import prgrms.neoike.controller.dto.drawdto.ItemSizeAndQuantity;
import prgrms.neoike.service.dto.drawdto.ServiceDrawSaveDto;
import prgrms.neoike.service.dto.drawdto.ServiceItemDto;

@Component
public class DrawMapper {
    public ServiceDrawSaveDto toDrawSaveDto(DrawSaveRequest drawSaveRequest) {
        return new ServiceDrawSaveDto(
                drawSaveRequest.sneakerId(),
                drawSaveRequest.startDate(),
                drawSaveRequest.endDate(),
                drawSaveRequest.winningDate(),
                drawSaveRequest.sneakerItems().stream()
                        .map(this::toItemDto)
                        .toList(),
                drawSaveRequest.quantity()
        );

    }

    private ServiceItemDto toItemDto(ItemSizeAndQuantity itemSizeAndQuantity){
        return new ServiceItemDto(
                itemSizeAndQuantity.size(),
                itemSizeAndQuantity.quantity()
        );
    }
}
