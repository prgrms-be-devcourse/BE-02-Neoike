package prgrms.neoike.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import prgrms.neoike.service.dto.memberdto.MemberDto;
import prgrms.neoike.controller.dto.MemberRequest;
import prgrms.neoike.controller.mapper.MemberMapper;
import prgrms.neoike.service.MemberService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public ResponseEntity<Long> joinMember(@Valid @RequestBody MemberRequest memberRequest) {
        MemberDto memberDto = MemberMapper.mapMemberDto(memberRequest);
        Long joinMemberId = memberService.join(memberDto);
        return ResponseEntity.ok(joinMemberId);
    }
}
