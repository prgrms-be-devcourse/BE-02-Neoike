package prgrms.neoike.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultActions;
import prgrms.neoike.config.SecurityApiTest;
import prgrms.neoike.controller.dto.drawdto.DrawSaveRequest;
import prgrms.neoike.controller.dto.drawdto.StockInfo;
import prgrms.neoike.domain.draw.DrawStatus;
import prgrms.neoike.service.DrawService;
import prgrms.neoike.service.DrawTicketService;
import prgrms.neoike.service.DrawWinnnerService;
import prgrms.neoike.service.dto.drawdto.DrawDto;
import prgrms.neoike.service.dto.drawdto.DrawResponse;
import prgrms.neoike.service.dto.drawdto.DrawSaveDto;
import prgrms.neoike.service.dto.drawdto.StockInfoDto;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketResponse;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketsResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DrawController.class)
class DrawControllerTest extends SecurityApiTest {

    @MockBean
    DrawService drawService;

    @MockBean
    DrawWinnnerService drawWinnnerService;

    @MockBean
    DrawTicketService drawTicketService;

    @Test
    @DisplayName("/api/v1/draws 에서 draws 저장")
    void postDraw() throws Exception {
        // given
        LocalDateTime fastDate = LocalDateTime.of(2025, 06, 12, 12, 00, 00);
        LocalDateTime middleDate = LocalDateTime.of(2025, 06, 13, 12, 00, 00);
        LocalDateTime lateDate = LocalDateTime.of(2025, 06, 15, 12, 00, 00);

        StockInfo sneakerItems = new StockInfo(275, 10);
        StockInfoDto sneakerItemsInService = new StockInfoDto(275,
            10);

        DrawSaveRequest drawSaveRequest = DrawSaveRequest.builder()
            .sneakerId(1L)
            .startDate(fastDate)
            .endDate(middleDate)
            .winningDate(lateDate)
            .sneakerStocks(new ArrayList<>() {{
                add(sneakerItems);
            }})
            .quantity(50)
            .build();

        DrawSaveDto drawSaveDto = DrawSaveDto.builder()
            .sneakerId(2L)
            .startDate(fastDate)
            .endDate(middleDate)
            .winningDate(lateDate)
            .sneakerStocks(new ArrayList<>() {{
                add(sneakerItemsInService);
            }})
            .quantity(50)
            .build();

        DrawResponse drawResponse = new DrawResponse(3L);

        given(drawService.save(any(DrawSaveDto.class))).willReturn(drawResponse);

        // when
        ResultActions resultActions = mockMvc.perform(
            post("/api/v1/draws")
                .content(objectMapper.writeValueAsString(drawSaveRequest))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(document(COMMON_DOCS_NAME,
                requestHeaders(commonHeaders()),
                requestFields(
                    fieldWithPath("sneakerId").type(NUMBER).description("신발 아이디"),
                    fieldWithPath("startDate").type(STRING).description("응모 시작 날짜"),
                    fieldWithPath("endDate").type(STRING).description("응모 종료 날짜"),
                    fieldWithPath("winningDate").type(STRING).description("추첨 날짜"),
                    fieldWithPath("quantity").type(NUMBER).description("응모 개수"),
                    fieldWithPath("winningDate").type(STRING).description("추첨 날짜"),
                    fieldWithPath("sneakerStocks").type(ARRAY).description("응모 상품들"),
                    fieldWithPath("sneakerStocks[].size").type(NUMBER).description("응모 상품 사이즈"),
                    fieldWithPath("sneakerStocks[].quantity").type(NUMBER)
                        .description("응모 상품 응모 개수")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.LOCATION).description("로케이션"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                ),
                responseFields(
                    fieldWithPath("drawId").type(NUMBER).description("생성된 응모 id")
                )));
    }

    @Test
    @DisplayName("/api/v1/draws/win 에서 추첨을 진행한다")
    void postDrawWinner() throws Exception {
        // given
        DrawTicketsResponse drawTicketResponses = new DrawTicketsResponse(
            Arrays.asList(
                DrawTicketResponse.builder()
                    .drawTicketId(1L)
                    .drawStatus(DrawStatus.WINNING)
                    .sneakerName("air jordan")
                    .price(27500)
                    .code("AB1234")
                    .size(275)
                    .build()
            )
        );

        given(drawWinnnerService.drawWinner(1L)).willReturn(drawTicketResponses);

        // when
        ResultActions resultActions = mockMvc.perform(
            post("/api/v1/draws/win").param("drawId", String.valueOf(1L)));

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
                responseFields(
                    fieldWithPath("drawTicketResponses").type(ARRAY).description("응모권 리스트"),
                    fieldWithPath("drawTicketResponses[].drawTicketId").type(NUMBER)
                        .description("응모권 아이디"),
                    fieldWithPath("drawTicketResponses[].drawStatus").type(STRING)
                        .description("응모권 상태"),
                    fieldWithPath("drawTicketResponses[].sneakerName").type(STRING)
                        .description("신발 이름"),
                    fieldWithPath("drawTicketResponses[].price").type(NUMBER).description("가격"),
                    fieldWithPath("drawTicketResponses[].code").type(STRING).description("코드"),
                    fieldWithPath("drawTicketResponses[].size").type(NUMBER).description("사이즈")
                )));
    }

    @Test
    @DisplayName("/api/v1/draws 에서 응모권 정보를 조회한다")
    void getDraws() throws Exception {
        // given
        given(drawService.getAvailableDraws()).willReturn(drawDtos());

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/draws", 1L));

        //then
        resultActions
            .andExpect(status().isOk())
            .andDo(
                document(COMMON_DOCS_NAME,
                    requestHeaders(
                        headerWithName(HttpHeaders.HOST).description("호스트")
                    ),
                    responseHeaders(
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입")
                    ),
                    responseFields()
                        .andWithPrefix("[]", drawDtoDescriptors())
                )
            );
    }

    private List<DrawDto> drawDtos() {
        return Arrays.asList(
            DrawDto.builder()
                .drawId(1L)
                .sneakerId(1L)
                .sneakerName("air jordan")
                .sneakerThumbnailPath("/images/air-jordan.jpg")
                .startDate(LocalDateTime.of(2025, 06, 12, 12, 00, 00))
                .endDate(LocalDateTime.of(2025, 06, 13, 12, 00, 00))
                .winningDate(LocalDateTime.of(2025, 06, 15, 12, 00, 00))
                .quantity(50)
                .build(),

            DrawDto.builder()
                .drawId(2L)
                .sneakerId(2L)
                .sneakerName("air max")
                .sneakerThumbnailPath("/images/air-max.jpg")
                .startDate(LocalDateTime.of(2025, 06, 12, 12, 00, 00))
                .endDate(LocalDateTime.of(2025, 06, 13, 12, 00, 00))
                .winningDate(LocalDateTime.of(2025, 06, 15, 12, 00, 00))
                .quantity(30)
                .build()
        );
    }

    private List<FieldDescriptor> drawDtoDescriptors() {
        return List.of(
            fieldWithPath("drawId").type(NUMBER).description("응모 아이디"),
            fieldWithPath("startDate").type(STRING).description("응모 시작 날짜 "),
            fieldWithPath("endDate").type(STRING).description("응모 종료 날짜"),
            fieldWithPath("winningDate").type(STRING).description("응모 당첨 날짜"),
            fieldWithPath("quantity").type(NUMBER).description("응모 개수"),
            fieldWithPath("sneakerId").type(NUMBER).description("신발 아이디"),
            fieldWithPath("sneakerName").type(STRING).description("신발 이름"),
            fieldWithPath("sneakerThumbnailPath").type(STRING).description("신발 사진 파일 주소")
        );
    }

    private List<HeaderDescriptor> commonHeaders() {
        return List.of(
            headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입"),
            headerWithName(HttpHeaders.CONTENT_LENGTH).description("컨텐츠 길이"),
            headerWithName(HttpHeaders.HOST).description("호스트")
        );
    }
}