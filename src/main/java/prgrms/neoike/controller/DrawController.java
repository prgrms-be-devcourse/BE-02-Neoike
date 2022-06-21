package prgrms.neoike.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prgrms.neoike.controller.dto.drawdto.DrawSaveRequest;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketResponse;
import prgrms.neoike.controller.mapper.DrawMapper;
import prgrms.neoike.service.DrawService;
import prgrms.neoike.service.DrawTicketService;
import prgrms.neoike.service.dto.drawdto.DrawResponse;
import prgrms.neoike.service.dto.drawdto.ServiceDrawSaveDto;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DrawController {
    private final DrawService drawService;
    private final DrawTicketService drawTicketService;
    private final DrawMapper drawMapper;

    @PostMapping("/draws")
    public ResponseEntity<DrawResponse> saveDraw(
            @Valid @RequestBody DrawSaveRequest saveRequest
    ) {
        ServiceDrawSaveDto serviceDrawSaveDto = drawMapper.toDrawSaveDto(saveRequest);
        DrawResponse drawResponse = drawService.save(serviceDrawSaveDto);

        // Sneaker Item 등록
        // Long sneakerId = saveRequest.sneakerId();
        // ArrayList<DrawItem> drawItems = saveRequest.sneakerItems();
        // sneakerService.itemRegistration(sneakerId, drawItems);

        return ResponseEntity
                .ok()
                .body(drawResponse);
    }

    @PostMapping("/draw-sneakers")
    public ResponseEntity<DrawTicketResponse> registerDrawTicket(
            @RequestParam Long memberId,
            @RequestParam Long drawId
    ) {
        DrawTicketResponse drawTicketResponse = drawTicketService.saveDrawTicket(memberId, drawId);
        return ResponseEntity
                .ok()
                .body(drawTicketResponse);
    }
}
