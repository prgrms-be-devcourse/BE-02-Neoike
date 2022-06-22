package prgrms.neoike.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import prgrms.neoike.controller.mapper.DrawMapper;
import prgrms.neoike.service.DrawTicketService;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketResponse;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketListResponse;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DrawTicketController.class)
@AutoConfigureRestDocs
class DrawTicketControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    DrawTicketService drawTicketService;

    @MockBean
    DrawMapper drawMapper;

    @Test
    @DisplayName("/api/v1/draw-sneakers 에서 응모권 저장")
    void saveDrawTicketTest() throws Exception {
        // given
        DrawTicketResponse drawTicketResponse = new DrawTicketResponse(3L);
        given(drawTicketService.save(1L, 2L)).willReturn(drawTicketResponse);

        // when // then
        mockMvc.perform(post("/api/v1/draw-sneakers")
                        .param("memberId", String.valueOf(1L))
                        .param("drawId", String.valueOf(2L))
                )
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("save-draw-ticket",
                        responseFields(
                                fieldWithPath("drawTicketId").type(NUMBER).description("발행된 응모권 id")
                        )));
    }

    @Test
    @DisplayName("/api/v1/draw-sneakers/{id} 에서 멤버 아이디를 통해 멤버가 응모한 응모권목록을 확인한다")
    void findDrawTicketsByMemberIdTest() throws Exception {
        // given
        DrawTicketListResponse drawTicketResponses = new DrawTicketListResponse(
                Arrays.asList(
                        new DrawTicketResponse(1L),
                        new DrawTicketResponse(2L),
                        new DrawTicketResponse(3L)
                )
        );
        doReturn(drawTicketResponses).when(drawTicketService).findByMember(1L);

        // when // then
        mockMvc.perform(get("/api/v1/draw-sneakers/{memberId}", 1L))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("find-drawTickets-by-memberId",
                        responseFields(
                                fieldWithPath("drawTicketResponses").type(ARRAY).description("응모권 배열"),
                                fieldWithPath("drawTicketResponses[].drawTicketId").type(NUMBER).description("응모권 아이디")
                        )));

    }

}