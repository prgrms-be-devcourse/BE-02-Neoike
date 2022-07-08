package prgrms.neoike.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import prgrms.neoike.controller.dto.member.MemberSaveRequest;
import prgrms.neoike.controller.mapper.MemberMapper;
import prgrms.neoike.service.MemberService;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketsResponse;
import prgrms.neoike.service.dto.member.MemberDto;
import prgrms.neoike.service.dto.member.MemberResponse;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberResponse> joinMember(
        @Valid @RequestBody MemberSaveRequest memberSaveRequest
    ) {
        MemberDto memberDto = MemberMapper.toMemberDto(memberSaveRequest);
        MemberResponse joinMemberResponse = memberService.join(memberDto);

        return ResponseEntity.created(
            URI.create("/api/v1/loginHome")
        ).body(joinMemberResponse);
    }

    @GetMapping("/draw-history")
    public ResponseEntity<DrawTicketsResponse> getMyDrawHistory(
        @AuthenticationPrincipal User authentication
    ) {
        DrawTicketsResponse myDrawHistory = memberService.getMyDrawHistory(authentication.getUsername());

        return ResponseEntity
            .ok()
            .body(myDrawHistory);
    }
}
