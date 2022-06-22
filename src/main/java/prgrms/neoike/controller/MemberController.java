package prgrms.neoike.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prgrms.neoike.common.jwt.JwtFilter;
import prgrms.neoike.controller.dto.memberdto.LoginRequest;
import prgrms.neoike.controller.dto.MemberRequest;
import prgrms.neoike.controller.dto.memberdto.TokenResponse;
import prgrms.neoike.controller.mapper.MemberMapper;
import prgrms.neoike.service.MemberService;
import prgrms.neoike.service.dto.memberdto.LoginDto;
import prgrms.neoike.service.dto.memberdto.MemberDto;
import prgrms.neoike.service.dto.memberdto.MemberResponse;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("")
    public ResponseEntity<MemberResponse> joinMember(@Valid @RequestBody MemberRequest memberRequest) {
        MemberDto memberDto = MemberMapper.mapMemberDto(memberRequest);
        MemberResponse joinMemberResponse = memberService.join(memberDto);
        return ResponseEntity.ok(joinMemberResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginDto loginDto = MemberMapper.toLoginDto(loginRequest);
        String jwt = memberService.login(loginDto);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return new ResponseEntity<>(new TokenResponse(jwt), httpHeaders, HttpStatus.OK);
    }
}
