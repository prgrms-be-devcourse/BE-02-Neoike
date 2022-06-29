package prgrms.neoike.service.converter;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import lombok.NoArgsConstructor;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.domain.draw.DrawTicket;
import prgrms.neoike.domain.sneaker.Sneaker;
import prgrms.neoike.service.dto.drawdto.DrawDto;
import prgrms.neoike.service.dto.drawdto.DrawResponse;
import prgrms.neoike.service.dto.drawdto.DrawSaveDto;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketResponse;

@NoArgsConstructor(access = PRIVATE)
public class DrawConverter {

    public static Draw toDraw(DrawSaveDto drawSaveRequest, Sneaker sneaker) {
        return Draw.builder()
            .sneaker(sneaker)
            .startDate(drawSaveRequest.startDate())
            .endDate(drawSaveRequest.endDate())
            .winningDate(drawSaveRequest.winningDate())
            .quantity(drawSaveRequest.quantity())
            .build();
    }

    public static DrawResponse toDrawResponseDto(Long id) {
        return new DrawResponse(id);
    }

    public static DrawTicketResponse toDrawTicketResponse(DrawTicket drawTicket) {
        return DrawTicketResponse.builder()
            .drawTicketId(drawTicket.getId())
            .drawStatus(drawTicket.getDrawStatus())
            .sneakerName(drawTicket.getSneakerName())
            .price(drawTicket.getPrice())
            .code(drawTicket.getCode())
            .size(drawTicket.getSize())
            .build();
    }

    public static DrawDto toDrawDto(Draw draw) {
        String thumbnailPath = draw.getSneaker().getSneakerImages()
            .stream().toList().get(0).getPath();

        return DrawDto.builder()
            .drawId(draw.getId())
            .startDate(draw.getStartDate())
            .endDate(draw.getEndDate())
            .winningDate(draw.getWinningDate())
            .sneakerId(draw.getSneaker().getId())
            .sneakerName(draw.getSneaker().getName())
            .sneakerThumbnailPath(thumbnailPath)
            .quantity(draw.getQuantity())
            .build();
    }

    public static List<DrawDto> toDrawDtos(List<Draw> draws) {
        return draws.stream()
            .map(DrawConverter::toDrawDto)
            .toList();
    }
}