package prgrms.neoike.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import prgrms.neoike.common.jwt.JwtFilter;
import prgrms.neoike.common.jwt.TokenProvider;
import prgrms.neoike.config.SecurityApiTest;
import prgrms.neoike.controller.dto.member.MemberSaveRequest;
import prgrms.neoike.domain.draw.DrawStatus;
import prgrms.neoike.domain.member.CountryType;
import prgrms.neoike.domain.member.Gender;
import prgrms.neoike.service.CustomUserDetailService;
import prgrms.neoike.service.MemberService;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketResponse;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketsResponse;
import prgrms.neoike.service.dto.member.MemberResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest extends SecurityApiTest {

    @MockBean
    private MemberService memberService;

    @Autowired
    private TokenProvider tokenProvider;

    @MockBean
    private CustomUserDetailService customUserDetailService;

    @Test
    @WithMockUser
    @DisplayName("멤버 회원가입 요청을 테스트한다")
    void postMember() throws Exception {

        String content = objectMapper.writeValueAsString(
            new MemberSaveRequest(
                "test@gmail.com",
                "testPassword123!",
                "testUser",
                LocalDate.now(),
                "Seoul",
                "samsungro",
                "1234",
                CountryType.KOR,
                "01012345678",
                Gender.FEMALE
            ));

        given(memberService.join(any()))
            .willReturn(new MemberResponse(1L, "test@gmail.com"));


        ResultActions result = mockMvc.perform(post("/api/v1/members")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            .andExpect(status().isCreated())
            .andDo(print());

        result.andExpect(status().isCreated())
            .andDo(document("{method-name}",
                requestFields(
                    fieldWithPath("email").type(JsonFieldType.STRING).description("이메일형식의 로그인아이디"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                    fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                    fieldWithPath("birthday").type(JsonFieldType.STRING)
                        .attributes(key("format").value("yyyy-MM-dd hh:mm:ss"))
                        .description("생일"),
                    fieldWithPath("city").type(JsonFieldType.STRING).description("거주도시명"),
                    fieldWithPath("street").type(JsonFieldType.STRING).description("거주도로명"),
                    fieldWithPath("zipcode").type(JsonFieldType.STRING).description("우편번호"),
                    fieldWithPath("countryCode").type(JsonFieldType.STRING).description("휴대전화 국가코드"),
                    fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("휴대전화번호"),
                    fieldWithPath("gender").type(JsonFieldType.STRING).description("성별: MALE(남성)/FEMALE(여성)")
                ),
                responseFields(
                    fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("아이디"),
                    fieldWithPath("email").type(JsonFieldType.STRING).description("로그인아이디")
                )
            ));
    }

    @Test
    @DisplayName("인증(로그인)이 유효하면 응모정보를 가져온다")
    void getDrawHistory() throws Exception {
        String email = "test@gmail.com";
        String password = "1234!abcD";
        UserDetails dummy = new User(email, password, List.of());
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
        String token = tokenProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, JwtFilter.AUTHORIZATION_TYPE + token);

        DrawTicketResponse size = DrawTicketResponse.builder()
            .drawTicketId(1L)
            .drawStatus(DrawStatus.WAITING)
            .sneakerName("테스트 신발")
            .price(100000)
            .code("TEST_CODE")
            .size(250)
            .build();

        List<DrawTicketResponse> drawTicketResponses = new ArrayList<>();
        drawTicketResponses.add(size);
        DrawTicketsResponse myDrawHistory = new DrawTicketsResponse(drawTicketResponses);

        when(customUserDetailService.loadUserByUsername(email))
            .thenReturn(dummy);
        when(memberService.getMyDrawHistory())
            .thenReturn(myDrawHistory);

        ResultActions result = mockMvc.perform(get("/api/v1/members/draw-history")
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print());

        result.andExpect(status().isOk())
            .andDo(document("{method-name}",
                responseFields(
                    fieldWithPath("drawTicketResponses[]").type(JsonFieldType.ARRAY).description("응모이력"),
                    fieldWithPath("drawTicketResponses[].drawTicketId").type(JsonFieldType.NUMBER).description("응모이력 아이디"),
                    fieldWithPath("drawTicketResponses[].drawStatus").type(JsonFieldType.STRING).description("응모 상태"),
                    fieldWithPath("drawTicketResponses[].sneakerName").type(JsonFieldType.STRING).description("응모 신발명"),
                    fieldWithPath("drawTicketResponses[].price").type(JsonFieldType.NUMBER).description("응모 신발 가격"),
                    fieldWithPath("drawTicketResponses[].code").type(JsonFieldType.STRING).description("응모 신발 코드"),
                    fieldWithPath("drawTicketResponses[].size").type(JsonFieldType.NUMBER).description("응모 신발 사이즈")
                )
            ));
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