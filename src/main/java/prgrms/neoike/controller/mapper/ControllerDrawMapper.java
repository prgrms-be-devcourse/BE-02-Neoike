package prgrms.neoike.controller.mapper;

import org.springframework.stereotype.Component;
import prgrms.neoike.controller.dto.drawdto.DrawSaveRequest;
import prgrms.neoike.service.dto.drawdto.ServiceDrawSaveDto;

@Component
public class ControllerDrawMapper {
    public ServiceDrawSaveDto convertToServiceDrawSaveDto(DrawSaveRequest drawSaveRequest) {
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
