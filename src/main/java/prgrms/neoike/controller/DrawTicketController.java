package prgrms.neoike.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prgrms.neoike.controller.mapper.DrawMapper;
import prgrms.neoike.service.DrawTicketService;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketResponse;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketListResponse;

import java.net.URI;

import static java.text.MessageFormat.format;

@RestController
@RequestMapping("/api/v1/draw-sneakers")
@RequiredArgsConstructor
public class DrawTicketController {
    private final DrawTicketService drawTicketService;
    private final DrawMapper drawMapper;

    @PostMapping
    public ResponseEntity<DrawTicketResponse> saveDrawTicket(
            @RequestParam Long memberId,
            @RequestParam Long drawId
    ) {
        DrawTicketResponse drawTicketResponse = drawTicketService.save(memberId, drawId);
        URI location = URI.create(format("/api/v1/draw-sneakers/{0}", drawTicketResponse.drawTicketId()));

        return ResponseEntity
                .created(location)
                .body(drawTicketResponse);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<DrawTicketListResponse> findByMemberId(
            @PathVariable Long memberId
    ) {
        DrawTicketListResponse drawTicketResponses = drawTicketService.findByMember(memberId);

        return ResponseEntity
                .ok()
                .body(drawTicketResponses);
    }
}
