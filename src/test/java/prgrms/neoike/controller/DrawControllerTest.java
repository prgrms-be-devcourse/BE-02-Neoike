package prgrms.neoike.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.controller.dto.drawdto.DrawItem;
import prgrms.neoike.controller.dto.drawdto.DrawSaveRequest;
import prgrms.neoike.domain.member.Gender;
import prgrms.neoike.domain.member.Member;
import prgrms.neoike.repository.MemberRepository;
import prgrms.neoike.service.DrawService;
import prgrms.neoike.service.dto.drawdto.DrawResponse;
import prgrms.neoike.service.dto.drawdto.ServiceDrawSaveDto;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.payload.JsonFieldType.*;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
class DrawControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    DrawService drawService;

    @Autowired
    MemberRepository memberRepository;
    // 추후 예림님 코드 추가시 memberService 로 변경예정

    @Test
    @DisplayName("/api/v1/draws 에서 draws 저장")
    void saveDrawTest() throws Exception {
        // given
        LocalDateTime fastDate = LocalDateTime.of(2022, 06, 12, 12, 00, 00);
        LocalDateTime middleDate = LocalDateTime.of(2022, 06, 13, 12, 00, 00);
        LocalDateTime lateDate = LocalDateTime.of(2022, 06, 15, 12, 00, 00);

        DrawSaveRequest drawSaveRequest = new DrawSaveRequest(
                1L, fastDate, middleDate, lateDate, 50,
                new ArrayList<DrawItem>() {{
                    add(new DrawItem(1L, 10));
                    add(new DrawItem(2L, 20));
                }}
        );
        // when // then
        mockMvc.perform(post("/api/v1/draws")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(drawSaveRequest))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("save-draw",
                        requestFields(
                                fieldWithPath("sneakerId").type(NUMBER).description("sneaker id"),
                                fieldWithPath("startDate").type(STRING).description("응모 시작 날짜"),
                                fieldWithPath("endDate").type(STRING).description("응모 종료 날짜"),
                                fieldWithPath("winningDate").type(STRING).description("추첨 날짜"),
                                fieldWithPath("quantity").type(NUMBER).description("응모 개수"),
                                fieldWithPath("sneakerItems").type(ARRAY).description("sneakerItem 배열"),
                                fieldWithPath("sneakerItems[].sneakerItemId").type(NUMBER).description("sneakerItem id"),
                                fieldWithPath("sneakerItems[].quantity").type(NUMBER).description("해당 sneakerItem 개수")
                        ),
                        responseFields(
                                fieldWithPath("drawId").type(NUMBER).description("생성된 응모 id")
                        )));
    }


    @Test
    @DisplayName("/api/v1/draws 에서 draws 저장 실패")
    void invalidSaveDrawTest() throws Exception {
        // given
        LocalDateTime fastDate = LocalDateTime.of(2022, 06, 12, 12, 00, 00);
        LocalDateTime middleDate = LocalDateTime.of(2022, 06, 13, 12, 00, 00);
        LocalDateTime lateDate = LocalDateTime.of(2022, 06, 15, 12, 00, 00);

        DrawSaveRequest drawSaveRequest = new DrawSaveRequest(
                1L, middleDate, fastDate, lateDate, 50,
                new ArrayList<DrawItem>() {{
                    add(new DrawItem(1L, 10));
                    add(new DrawItem(2L, 20));
                }}
        );
        // when // then
        mockMvc.perform(post("/api/v1/draws")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(drawSaveRequest))
                )
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andDo(document("invalid-save-draw",
                        requestFields(
                                fieldWithPath("sneakerId").type(NUMBER).description("sneaker id"),
                                fieldWithPath("startDate").type(STRING).description("응모 시작 날짜"),
                                fieldWithPath("endDate").type(STRING).description("응모 종료 날짜"),
                                fieldWithPath("winningDate").type(STRING).description("추첨 날짜"),
                                fieldWithPath("quantity").type(NUMBER).description("응모 개수"),
                                fieldWithPath("sneakerItems").type(ARRAY).description("sneakerItem 배열"),
                                fieldWithPath("sneakerItems[].sneakerItemId").type(NUMBER).description("sneakerItem id"),
                                fieldWithPath("sneakerItems[].quantity").type(NUMBER).description("해당 sneakerItem 개수")
                        ),
                        responseFields(
                                fieldWithPath("message").type(STRING).description("오류 메세지"),
                                fieldWithPath("cause").type(STRING).description("오류 원인 메세지")
                        )));
    }

    @Test
    @DisplayName("/api/v1/draw-sneakers 에서 응모권 저장")
    @Transactional
    void saveDrawTicketTest() throws Exception {
        // given
        LocalDateTime fastDate = LocalDateTime.of(2022, 06, 12, 12, 00, 00);
        LocalDateTime middleDate = LocalDateTime.of(2022, 06, 13, 12, 00, 00);
        LocalDateTime lateDate = LocalDateTime.of(2022, 06, 15, 12, 00, 00);

        DrawResponse save = drawService.save(new ServiceDrawSaveDto(
                1L, fastDate, middleDate, lateDate, 50, new ArrayList<DrawItem>() {{
            add(new DrawItem(1L, 10));
            add(new DrawItem(2L, 20));
        }}
        ));
        // 추후 예림님 코드 추가 시 null 변경 예정, memberRepostory -> memberService 변경 예정
        Member member = memberRepository.save(new Member(
                "이용훈", null, null, LocalDateTime.now(), null, null, Gender.MALE));

        Long memberId = member.getId();
        Long drawId = save.drawId();

        // when // then
        mockMvc.perform(post("/api/v1//draw-sneakers")
                        .param("memberId", String.valueOf(memberId))
                        .param("drawId", String.valueOf(drawId))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("save-draw-ticket",
                        responseFields(
                                fieldWithPath("drawTicketId").type(NUMBER).description("발행된 응모권 id")
                        )));
    }
}