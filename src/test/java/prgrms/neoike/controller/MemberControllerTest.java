package prgrms.neoike.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import prgrms.neoike.common.config.SecurityConfig;
import prgrms.neoike.controller.dto.memberdto.MemberSaveRequest;
import prgrms.neoike.controller.dto.memberdto.MemberLoginRequest;
import prgrms.neoike.domain.member.CountryType;
import prgrms.neoike.domain.member.Gender;
import prgrms.neoike.service.MemberService;
import prgrms.neoike.service.dto.memberdto.MemberResponse;

import java.time.LocalDateTime;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = MemberController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class)
        })
@MockBean(JpaMetamodelMappingContext.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

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
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 요청을 테스트한다")
    void loginTest() throws Exception {
        String content = objectMapper.writeValueAsString(new MemberLoginRequest("test@gmail.com", "1234!abcA"));
        String secret = "cHJncm1zLWJlLWRldmNvdXJzZS1CRS0wMi1OZW9pa2Utc3ByaW5nLWJvb3QtYmFja2VuZC1wcm9qZWN0LWp3dC10b2tlbi1wcmdybXMtYmUtZGV2Y291cnNlLUJFLTAyLU5lb2lrZS1zcHJpbmctYm9vdC1iYWNrZW5kLXByb2plY3Qtand0LXRva2Vu";
        String jwt = Jwts.builder()
                .setSubject("test@gmail.com")
                .claim("auth", "")
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)), SignatureAlgorithm.HS512)
                .setExpiration(new Date(new Date().getTime() + 86000 * 1000))
                .compact();
        when(memberService.login(any())).thenReturn(jwt);

        mockMvc.perform(post("/api/v1/members/login")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}