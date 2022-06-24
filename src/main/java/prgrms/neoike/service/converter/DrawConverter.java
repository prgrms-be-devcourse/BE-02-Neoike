package prgrms.neoike.service.converter;

import org.springframework.stereotype.Component;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.domain.draw.DrawTicket;
import prgrms.neoike.domain.sneaker.Sneaker;
import prgrms.neoike.service.dto.drawdto.DrawResponse;
import prgrms.neoike.service.dto.drawdto.ServiceDrawSaveDto;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketResponse;

@Component
public class DrawConverter {
    public Draw toDraw(ServiceDrawSaveDto drawSaveRequest, Sneaker sneaker) {
        return Draw.builder()
                .sneaker(sneaker)
                .startDate(drawSaveRequest.startDate())
                .endDate(drawSaveRequest.endDate())
                .winningDate(drawSaveRequest.winningDate())
                .quantity(drawSaveRequest.quantity())
                .build();
    }

    public DrawResponse toDrawResponseDto(Long id) {
        return new DrawResponse(id);
    }

    public DrawTicketResponse toDrawTicketResponse(DrawTicket drawTicket) {
        return DrawTicketResponse.builder()
                .drawTicketId(drawTicket.getId())
                .drawStatus(drawTicket.getDrawStatus())
                .sneakerName(drawTicket.getSneakerName())
                .price(drawTicket.getPrice())
                .code(drawTicket.getCode())
                .size(drawTicket.getSize())
                .build();
    }

}
