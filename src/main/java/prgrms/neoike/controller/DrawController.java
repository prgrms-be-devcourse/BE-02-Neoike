package prgrms.neoike.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prgrms.neoike.controller.dto.drawdto.DrawSaveRequest;
import prgrms.neoike.controller.mapper.DrawMapper;
import prgrms.neoike.service.DrawService;
import prgrms.neoike.service.dto.drawdto.DrawResponse;
import prgrms.neoike.service.dto.drawdto.ServiceDrawSaveDto;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketListResponse;

import javax.validation.Valid;
import java.net.URI;

import static java.text.MessageFormat.format;


@RestController
@RequestMapping("/api/v1/draws")
@RequiredArgsConstructor
public class DrawController {
    private final DrawService drawService;
    private final DrawMapper drawMapper;

    @PostMapping
    public ResponseEntity<DrawResponse> saveDraw(
            @Valid @RequestBody DrawSaveRequest saveRequest
    ) {
        ServiceDrawSaveDto serviceDrawSaveDto = drawMapper.toDrawSaveDto(saveRequest);
        DrawResponse drawResponse = drawService.save(serviceDrawSaveDto);

        // Sneaker Item 등록
        // Long stockId = saveRequest.stockId();
        // ArrayList<DrawItem> drawItems = saveRequest.sneakerItems();
        // sneakerService.itemRegistration(stockId, drawItems);

        URI location = URI.create(format("/api/v1/draws/{0}", drawResponse.drawId()));
        return ResponseEntity
                .created(location)
                .body(drawResponse);
    }

    @PostMapping("/win")
    public ResponseEntity<DrawTicketListResponse> winDraw(
            @RequestParam Long drawId
    ) {
        DrawTicketListResponse winningTicketsResponse = drawService.drawWinner(drawId);

        return ResponseEntity
                .ok()
                .body(winningTicketsResponse);
    }
}
