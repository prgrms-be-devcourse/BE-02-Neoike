package prgrms.neoike.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import prgrms.neoike.controller.dto.MemberRequest;
import prgrms.neoike.domain.member.CountryType;
import prgrms.neoike.domain.member.Gender;
import prgrms.neoike.service.MemberService;
import prgrms.neoike.service.dto.memberdto.MemberResponse;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
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
                new MemberRequest(
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
}