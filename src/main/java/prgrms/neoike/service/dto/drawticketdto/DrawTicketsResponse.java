package prgrms.neoike.service.dto.drawticketdto;

import java.util.List;

public record DrawTicketsResponse(
        List<DrawTicketResponse> drawTicketResponses
){
}
