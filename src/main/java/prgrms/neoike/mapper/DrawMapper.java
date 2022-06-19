package prgrms.neoike.mapper;

import org.springframework.stereotype.Component;
import prgrms.neoike.controller.dto.drawdto.DrawSaveRequest;
import prgrms.neoike.domain.draw.Draw;

@Component
public class DrawMapper {
    public Draw convertDraw(DrawSaveRequest drawSaveRequest) {
        return Draw.builder()
                .startDate(drawSaveRequest.startDate())
                .endDate(drawSaveRequest.endDate())
                .winningDate(drawSaveRequest.winningDate())
                .quantity(drawSaveRequest.quantity())
                .build();
    }
}
