package prgrms.neoike.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import prgrms.neoike.common.jwt.JwtFilter;
import prgrms.neoike.common.jwt.TokenProvider;
import prgrms.neoike.config.SecurityApiTest;
import prgrms.neoike.controller.dto.memberdto.MemberSaveRequest;
import prgrms.neoike.domain.member.CountryType;
import prgrms.neoike.domain.member.Gender;
import prgrms.neoike.service.CustomUserDetailService;
import prgrms.neoike.service.MemberService;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketsResponse;
import prgrms.neoike.service.dto.memberdto.MemberResponse;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest extends SecurityApiTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @Autowired
    private TokenProvider tokenProvider;

    @MockBean
    private CustomUserDetailService customUserDetailService;

    @Test
    @WithMockUser
    @DisplayName("멤버 회원가입 요청을 테스트한다")
    void joinMemberTest() throws Exception {

        String content = objectMapper.writeValueAsString(
                new MemberSaveRequest(
                        "test@gmail.com",
                        "testPassword123!",
                        "testUser",
                        LocalDateTime.now(),
                        "Seoul",
                        "samsungro",
                        "1234",
                        CountryType.KOR,
                        "01012345678",
                        Gender.FEMALE
                ));

        given(memberService.join(any()))
                .willReturn(new MemberResponse(1L, "test@gmail.com"));


        mockMvc.perform(post("/api/v1/members")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("인증(로그인)이 유효하면 응모정보를 가져온다")
    void getMyDrawHistoryTest() throws Exception {
        String email = "test@gmail.com";
        String password = "1234!abcD";
        UserDetails dummy = new User(email, password, List.of());
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
        String token = tokenProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, JwtFilter.AUTHORIZATION_TYPE + token);

        when(customUserDetailService.loadUserByUsername(email))
                .thenReturn(dummy);

        mockMvc.perform(get("/api/v1/members/draw-history")
                        .headers(httpHeaders)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("헤더에 토큰이 없으면 history api에 접근 할 수 없다")
    void validateJwtTokenTest() throws Exception {
        DrawTicketsResponse drawTicketListResponse = new DrawTicketsResponse(List.of());
        when(memberService.getMyDrawHistory())
                .thenReturn(drawTicketListResponse);

        mockMvc.perform(get("/api/v1/members/draw-history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}