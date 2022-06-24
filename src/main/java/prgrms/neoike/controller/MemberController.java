package prgrms.neoike.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prgrms.neoike.common.jwt.JwtFilter;
import prgrms.neoike.controller.dto.memberdto.MemberLoginRequest;
import prgrms.neoike.controller.dto.memberdto.MemberSaveRequest;
import prgrms.neoike.controller.dto.memberdto.TokenResponse;
import prgrms.neoike.controller.mapper.MemberMapper;
import prgrms.neoike.service.MemberService;
import prgrms.neoike.service.dto.memberdto.LoginDto;
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

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody MemberLoginRequest memberLoginRequest) {
        LoginDto loginDto = MemberMapper.toLoginDto(memberLoginRequest);
        String jwt = memberService.login(loginDto);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, JwtFilter.AUTHORIZATION_TYPE + jwt);

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(new TokenResponse(jwt));
    }
}
