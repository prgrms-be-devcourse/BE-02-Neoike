package prgrms.neoike.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prgrms.neoike.controller.dto.drawdto.DrawSaveRequest;
import prgrms.neoike.controller.mapper.DrawMapper;
import prgrms.neoike.service.DrawService;
import prgrms.neoike.service.dto.drawdto.DrawDto;
import prgrms.neoike.service.dto.drawdto.DrawResponse;
import prgrms.neoike.service.dto.drawdto.DrawSaveDto;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketsResponse;

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
        DrawSaveDto drawSaveDto = drawMapper.toDrawSaveDto(saveRequest);
        DrawResponse drawResponse = drawService.save(drawSaveDto);

        URI location = URI.create(format("/api/v1/draws/{0}", drawResponse.drawId()));
        return ResponseEntity
            .created(location)
            .body(drawResponse);
    }

    @PostMapping("/win")
    public ResponseEntity<DrawTicketsResponse> drawWinner(
        @RequestParam Long drawId
    ) {
        DrawTicketsResponse winningTicketsResponse = drawService.drawWinner(drawId);

        return ResponseEntity
            .ok()
            .body(winningTicketsResponse);
    }

    @GetMapping
    public ResponseEntity<List<DrawDto>> getAvailableDraws() {
        List<DrawDto> draws = drawService.getAvailableDraws();

        return ResponseEntity.ok(draws);
    }
}