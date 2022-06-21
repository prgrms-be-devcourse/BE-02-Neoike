package prgrms.neoike.controller.mapper;

import org.springframework.stereotype.Component;
import prgrms.neoike.controller.dto.drawdto.DrawSaveRequest;
import prgrms.neoike.service.dto.drawdto.ServiceDrawSaveDto;

@Component
public class DrawMapper {
    public ServiceDrawSaveDto toDrawSaveDto(DrawSaveRequest drawSaveRequest) {
        return new ServiceDrawSaveDto(
                drawSaveRequest.sneakerId(),
                drawSaveRequest.startDate(),
                drawSaveRequest.endDate(),
                drawSaveRequest.winningDate(),
                drawSaveRequest.quantity(),
                drawSaveRequest.sneakerItems()
        );
    }
}
