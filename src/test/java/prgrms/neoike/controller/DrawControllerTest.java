package prgrms.neoike.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import prgrms.neoike.controller.dto.drawdto.DrawItem;
import prgrms.neoike.controller.dto.drawdto.DrawSaveRequest;
import prgrms.neoike.controller.mapper.DrawMapper;
import prgrms.neoike.service.DrawService;
import prgrms.neoike.service.DrawTicketService;
import prgrms.neoike.service.dto.drawdto.DrawResponse;
import prgrms.neoike.service.dto.drawdto.ServiceDrawSaveDto;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.payload.JsonFieldType.*;


@WebMvcTest(DrawController.class)
@AutoConfigureRestDocs
class DrawControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    DrawService drawService;

    @MockBean
    DrawTicketService drawTicketService;

    @MockBean
    DrawMapper drawMapper;

    @Test
    @DisplayName("/api/v1/draws 에서 draws 저장")
    void saveDrawTest() throws Exception {
        // given
        LocalDateTime fastDate = LocalDateTime.of(2025, 06, 12, 12, 00, 00);
        LocalDateTime middleDate = LocalDateTime.of(2025, 06, 13, 12, 00, 00);
        LocalDateTime lateDate = LocalDateTime.of(2025, 06, 15, 12, 00, 00);

        DrawSaveRequest drawSaveRequest = DrawSaveRequest.builder()
                .sneakerId(1L)
                .startDate(fastDate)
                .endDate(middleDate)
                .winningDate(lateDate)
                .quantity(50)
                .sneakerItems(new ArrayList<DrawItem>() {{
                    add(new DrawItem(1L, 10));
                    add(new DrawItem(2L, 20));
                }})
                .build();

        ServiceDrawSaveDto serviceDrawSaveDto = ServiceDrawSaveDto.builder()
                .sneakerId(1L)
                .startDate(fastDate)
                .endDate(middleDate)
                .winningDate(lateDate)
                .quantity(50)
                .sneakerItems(new ArrayList<DrawItem>() {{
                    add(new DrawItem(1L, 10));
                    add(new DrawItem(2L, 20));
                }})
                .build();

        DrawResponse drawResponse = new DrawResponse(3L);

        doReturn(serviceDrawSaveDto).when(drawMapper).toDrawSaveDto(any(DrawSaveRequest.class));
        doReturn(drawResponse).when(drawService).save(any(ServiceDrawSaveDto.class));


        // when // then
        mockMvc.perform(post("/api/v1/draws")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(drawSaveRequest))
                )
                .andExpect(status().isCreated())
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
    @DisplayName("/api/v1/draw-sneakers 에서 응모권 저장")
    void saveDrawTicketTest() throws Exception {
        // given
        DrawTicketResponse drawTicketResponse = new DrawTicketResponse(3L);

        doReturn(drawTicketResponse).when(drawTicketService).saveDrawTicket(1L, 2L);

        // when // then
        mockMvc.perform(post("/api/v1//draw-sneakers")
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
}