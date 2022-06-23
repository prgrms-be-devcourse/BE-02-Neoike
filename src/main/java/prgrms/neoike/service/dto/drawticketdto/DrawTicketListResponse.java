package prgrms.neoike.service.dto.drawticketdto;

import java.util.List;

public record DrawTicketListResponse(
        List<DrawTicketResponse> drawTicketResponses
){
}
