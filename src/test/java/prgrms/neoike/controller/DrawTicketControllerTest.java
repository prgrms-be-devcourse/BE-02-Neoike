package prgrms.neoike.controller;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultActions;
import prgrms.neoike.config.SecurityApiTest;
import prgrms.neoike.domain.draw.DrawStatus;
import prgrms.neoike.service.DrawTicketService;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketResponse;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketsResponse;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DrawTicketController.class)
class DrawTicketControllerTest extends SecurityApiTest {

    @MockBean
    DrawTicketService drawTicketService;

    @Test
    @DisplayName("/api/v1/draw-sneakers 에서 응모권 저장")
    void postDrawTicket() throws Exception {
        // given
        DrawTicketResponse drawTicketResponse = validDrawTicketResponse();

        given(drawTicketService.save(1L, 2L, 275)).willReturn(drawTicketResponse);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/draw-sneakers")
            .param("memberId", String.valueOf(1L))
            .param("drawId", String.valueOf(2L))
            .param("size", String.valueOf(275))
        );

        // then
        resultActions
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(document(COMMON_DOCS_NAME,
                requestHeaders(
                    headerWithName(HttpHeaders.HOST).description("호스트")
                ),
                responseFields(commonDrawTicket())
            ));
    }

    @Test
    @DisplayName("/api/v1/draw-sneakers/{id} 에서 멤버 아이디를 통해 멤버가 응모한 응모권목록을 확인한다")
    void getDrawTickets() throws Exception {
        // given
        DrawTicketsResponse drawTicketResponses = new DrawTicketsResponse(
            Arrays.asList(
                validDrawTicketResponse()
            )
        );

        given(drawTicketService.findByMemberId(1L)).willReturn(drawTicketResponses);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/draw-sneakers/{memberId}", 1L));

        // then
        resultActions
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document(COMMON_DOCS_NAME,
                requestHeaders(
                    headerWithName(HttpHeaders.HOST).description("호스트")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                ),
                responseFields()
                    .andWithPrefix("drawTicketResponses[].", commonDrawTicket())
            ));
    }

    private DrawTicketResponse validDrawTicketResponse() {
        return DrawTicketResponse.builder()
            .drawTicketId(1L)
            .drawStatus(DrawStatus.WAITING)
            .code("AB1234")
            .price(67500)
            .size(275)
            .sneakerName("air jordan")
            .build();
    }

    private List<FieldDescriptor> commonDrawTicket() {
        return List.of(
            fieldWithPath("drawTicketId").type(NUMBER).description("응모권 아이디"),
            fieldWithPath("drawStatus").type(STRING).description("응모권 상태"),
            fieldWithPath("sneakerName").type(STRING).description("신발 이름"),
            fieldWithPath("price").type(NUMBER).description("가격"),
            fieldWithPath("code").type(STRING).description("코드"),
            fieldWithPath("size").type(NUMBER).description("사이즈")
        );
    }
}