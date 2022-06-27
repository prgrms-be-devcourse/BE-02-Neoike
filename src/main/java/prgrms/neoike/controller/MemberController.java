package prgrms.neoike.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prgrms.neoike.controller.dto.memberdto.MemberSaveRequest;
import prgrms.neoike.controller.mapper.MemberMapper;
import prgrms.neoike.service.MemberService;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketsResponse;
import prgrms.neoike.service.dto.memberdto.MemberDto;
import prgrms.neoike.service.dto.memberdto.MemberResponse;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberResponse> joinMember(@Valid @RequestBody MemberSaveRequest memberSaveRequest) {
        MemberDto memberDto = MemberMapper.toMemberDto(memberSaveRequest);
        MemberResponse joinMemberResponse = memberService.join(memberDto);

        return ResponseEntity.created(
                URI.create("/api/v1/loginHome")
        ).body(joinMemberResponse);
    }

    @GetMapping("/draw-history")
    public ResponseEntity<DrawTicketsResponse> getMyDrawHistory() {
        DrawTicketsResponse myDrawHistory = memberService.getMyDrawHistory();

        return ResponseEntity
                .ok()
                .body(myDrawHistory);
    }
}
