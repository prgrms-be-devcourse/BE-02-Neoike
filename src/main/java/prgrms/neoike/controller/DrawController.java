package prgrms.neoike.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prgrms.neoike.controller.dto.drawdto.DrawItem;
import prgrms.neoike.controller.dto.drawdto.DrawResponse;
import prgrms.neoike.controller.dto.drawdto.DrawSaveRequest;
import prgrms.neoike.controller.dto.drawticketdto.DrawTicketResponse;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.domain.member.Member;
import prgrms.neoike.mapper.DrawMapper;
import prgrms.neoike.service.DrawService;
import prgrms.neoike.service.DrawTicketService;
import prgrms.neoike.service.MemberService;
import prgrms.neoike.service.SneakerService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DrawController {
    private final DrawService drawService;
    private final DrawTicketService drawTicketService;
    private final SneakerService sneakerService;
    private final MemberService memberService;
    private final DrawMapper drawMapper;

    @PostMapping("/draws")
    public ResponseEntity<DrawResponse> saveDraw(
            @RequestBody DrawSaveRequest saveRequest
    ) {
        Long sneakerId = saveRequest.sneakerId();
        ArrayList<DrawItem> drawItems = saveRequest.sneakerItems();

        Draw draw = drawMapper.convertDraw(saveRequest);
        Long drawId = drawService.save(draw);

        // Sneaker Item 등록
        // sneakerService.itemRegistration(sneakerId, drawItems);

        DrawResponse drawResponse = new DrawResponse(drawId);

        return ResponseEntity
                .ok()
                .body(drawResponse);
    }

    @PostMapping("/drawsneakers")
    public ResponseEntity<DrawTicketResponse> registerDrawTicket(
            HttpServletResponse response,
            @RequestParam Long memberId,
            @RequestParam Long drawId
    ) throws IOException {
        Member member = memberService.findById(memberId);
        Draw draw = drawService.findById(drawId);
        Long savedId = 0L;

        try{
            savedId = drawTicketService.saveDrawTicket(member, draw);
        }catch (Exception e){
            response.sendRedirect("예외 실패 알림 사이트");
        }

        DrawTicketResponse drawTicketResponse = new DrawTicketResponse(savedId);
        return ResponseEntity
                .ok()
                .body(drawTicketResponse);
    }
}
