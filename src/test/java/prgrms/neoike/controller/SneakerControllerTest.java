package prgrms.neoike.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.web.servlet.ResultActions;
import prgrms.neoike.config.SecurityApiTest;
import prgrms.neoike.controller.dto.sneaker.SneakerRegisterRequest;
import prgrms.neoike.controller.dto.sneaker.SneakerRequest;
import prgrms.neoike.controller.dto.sneaker.SneakerStockRequest;
import prgrms.neoike.service.SneakerService;
import prgrms.neoike.service.dto.page.PageResponse;
import prgrms.neoike.service.dto.sneaker.SneakerDetailResponse;
import prgrms.neoike.service.dto.sneaker.SneakerIdResponse;
import prgrms.neoike.service.dto.sneaker.SneakerResponse;
import prgrms.neoike.service.dto.sneaker.SneakerStockResponse;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static prgrms.neoike.domain.sneaker.MemberCategory.MEN;
import static prgrms.neoike.domain.sneaker.SneakerCategory.JORDAN;

@WebMvcTest(controllers = SneakerController.class)
class SneakerControllerTest extends SecurityApiTest {

    private static final String SNEAKER_PREFIX = "sneaker.";
    private static final String SNEAKER_STOCKS_PREFIX = "sneakerStocks[*].";

    @MockBean
    SneakerService sneakerService;

    @Test
    @DisplayName("?????? ????????? ??????????????? ???????????????.")
    void postSneaker() throws Exception {
        SneakerRegisterRequest registerRequest = createRegisterRequest();
        String requestString = objectMapper.writeValueAsString(registerRequest);
        SneakerIdResponse response = new SneakerIdResponse(1L, "DS-1234567");

        //given
        given(sneakerService.registerSneaker(any()))
            .willReturn(response);

        //when
        ResultActions resultActions = mockMvc
            .perform(
                post("/api/v1/sneakers")
                    .content(requestString)
                    .contentType(APPLICATION_JSON));

        //then
        resultActions
            .andExpectAll(
                status().isCreated(),
                content().json(objectMapper.writeValueAsString(response)))
            .andDo(
                document(COMMON_DOCS_NAME,
                    requestHeaders(commonHeaders()).and(host()),
                    requestFields(imagePaths())
                        .andWithPrefix(SNEAKER_PREFIX, commonSneaker())
                        .andWithPrefix(SNEAKER_STOCKS_PREFIX, sneakerStock()),
                    responseHeaders(commonHeaders()).and(location()),
                    responseFields(sneakerId(), sneakerCode())));
    }

    @Test
    @DisplayName("?????? ???????????? ????????? ????????? ???????????? ??? ??? ??????.")
    void getSneaker() throws Exception {
        SneakerDetailResponse response = createDetailResponse();

        //given
        given(sneakerService.getSneakerDetail(any()))
            .willReturn(response);

        //when
        ResultActions resultActions = mockMvc
            .perform(
                get("/api/v1/sneakers/{sneakerId}/{code}", 1, "DS-1234567"));

        //then
        resultActions
            .andExpectAll(
                status().isOk(),
                content().json(objectMapper.writeValueAsString(response)))
            .andDo(
                document(COMMON_DOCS_NAME,
                    pathParameters(sneakerIdPath(), codePath()),
                    responseHeaders(commonHeaders()),
                    responseFields()
                        .andWithPrefix(SNEAKER_STOCKS_PREFIX, sneakerId())
                        .andWithPrefix(SNEAKER_STOCKS_PREFIX, sneakerStock())
                        .andWithPrefix(SNEAKER_STOCKS_PREFIX, stockId())
                        .andWithPrefix(SNEAKER_PREFIX, sneakerId())
                        .andWithPrefix(SNEAKER_PREFIX, commonSneaker())
                        .andWithPrefix(SNEAKER_PREFIX, imagePaths())));
    }

    @Test
    @DisplayName("????????? ????????? ????????? ?????? ???????????? ??? ??? ??????.")
    void getSneakers() throws Exception {
        PageResponse<SneakerResponse> response = createPageResponseWithSneakerResponse();

        //given
        given(sneakerService.getSneakers(any()))
            .willReturn(response);

        //when
        ResultActions resultActions = mockMvc
            .perform(
                get("/api/v1/sneakers")
                    .queryParam("page", "1")
                    .queryParam("size", "1")
                    .queryParam("sortBy", "createdAt")
                    .queryParam("direction", "desc"));

        //then
        resultActions
            .andExpectAll(
                status().isOk(),
                content().json(objectMapper.writeValueAsString(response)))
            .andDo(
                document(COMMON_DOCS_NAME,
                    requestParameters(pageParam()),
                    responseHeaders(commonHeaders()),
                    responseFields(subsectionWithPath("contents").type(ARRAY).description("????????? ?????????"))
                        .and(page())));
    }

    @Test
    @DisplayName("????????? ?????? ????????? ???????????? ???????????? ?????? ????????? ?????? ????????? ?????? ?????? ??? ??????.")
    void putSneakerStock() throws Exception {
        SneakerStockRequest stockRequest = new SneakerStockRequest(250, -10);
        String requestString = objectMapper.writeValueAsString(stockRequest);
        SneakerStockResponse response = new SneakerStockResponse(1L, 250, 0, 2L);

        //given
        given(sneakerService.manageSneakerStock(any()))
            .willReturn(response);

        //when
        ResultActions resultActions = mockMvc
            .perform(
                put("/api/v1/sneakers/stocks/{stockId}", 1L)
                    .content(requestString)
                    .contentType(APPLICATION_JSON));

        //then
        resultActions
            .andExpectAll(
                status().isOk(),
                content().json(objectMapper.writeValueAsString(response)))
            .andDo(
                document(COMMON_DOCS_NAME,
                    requestHeaders(commonHeaders()).and(host()),
                    pathParameters(stockIdPath()),
                    requestFields(sneakerStock()),
                    responseHeaders(commonHeaders()),
                    responseFields(stockId()).and(sneakerStock()).and(sneakerId())));
    }

    private PageResponse<SneakerResponse> createPageResponseWithSneakerResponse() {
        return PageResponse.<SneakerResponse>builder()
            .contents(List.of(createSneakerResponse()))
            .page(1)
            .size(1)
            .sorted(true)
            .totalPages(1)
            .totalElements(1)
            .isFirst(true)
            .isLast(false)
            .build();
    }

    private SneakerDetailResponse createDetailResponse() {
        return new SneakerDetailResponse(
            List.of(
                new SneakerStockResponse(1L, 250, 10, 2L),
                new SneakerStockResponse(3L, 260, 10, 4L)),
            createSneakerResponse()
        );
    }

    private SneakerResponse createSneakerResponse() {
        return SneakerResponse
            .builder()
            .sneakerId(1L)
            .memberCategory(MEN)
            .sneakerCategory(JORDAN)
            .name("jordan 1")
            .price(100000)
            .description("this is test jordan.")
            .code("DS-1234567")
            .releaseDate(LocalDateTime.of(2022, 10, 10, 10, 0, 0))
            .imagePaths(List.of("/src/main/~"))
            .build();
    }

    private SneakerRegisterRequest createRegisterRequest() {
        return new SneakerRegisterRequest(
            List.of("/src/main/~"),
            SneakerRequest
                .builder()
                .memberCategory(MEN)
                .sneakerCategory(JORDAN)
                .name("jordan 1")
                .price(100000)
                .description("this is test jordan.")
                .code("DS-1234567")
                .releaseDate(LocalDateTime.of(2022, 10, 10, 10, 0, 0))
                .build(),
            List.of(
                new SneakerStockRequest(250, 10),
                new SneakerStockRequest(260, 10))
        );
    }

    private List<HeaderDescriptor> commonHeaders() {
        return List.of(
            headerWithName(HttpHeaders.CONTENT_TYPE).description("????????? ??????"),
            headerWithName(HttpHeaders.CONTENT_LENGTH).description("????????? ??????")
        );
    }

    private HeaderDescriptor location() {
        return headerWithName(HttpHeaders.LOCATION).description("??????????????? ??????");
    }

    private HeaderDescriptor host() {
        return headerWithName(HttpHeaders.HOST).description("?????????");
    }

    private FieldDescriptor imagePaths() {
        return fieldWithPath("imagePaths").type(ARRAY).description("?????? ????????? ??????").optional();
    }

    private ParameterDescriptor codePath() {
        return parameterWithName("code").description("??????");
    }

    private ParameterDescriptor stockIdPath() {
        return parameterWithName("stockId").description("?????? ?????????");
    }

    private ParameterDescriptor sneakerIdPath() {
        return parameterWithName("sneakerId").description("?????? ?????????");
    }

    private FieldDescriptor sneakerId() {
        return fieldWithPath("sneakerId").type(NUMBER).description("?????? ?????????");
    }

    private FieldDescriptor sneakerCode() {
        return fieldWithPath("code").type(STRING).description("??????");
    }

    private FieldDescriptor stockId() {
        return fieldWithPath("stockId").type(NUMBER).description("?????? ?????????");
    }

    private List<FieldDescriptor> sneakerStock() {
        return List.of(
            fieldWithPath("size").type(NUMBER).description("?????? ?????????"),
            fieldWithPath("quantity").type(NUMBER).description("?????? ??????")
        );
    }

    private List<FieldDescriptor> commonSneaker() {
        return List.of(
            fieldWithPath("memberCategory").type(STRING).description("?????? ????????????"),
            fieldWithPath("sneakerCategory").type(STRING).description("?????? ????????????"),
            fieldWithPath("name").type(STRING).description("??????"),
            fieldWithPath("price").type(NUMBER).description("??????"),
            fieldWithPath("description").type(STRING).description("??????"),
            fieldWithPath("code").type(STRING).description("??????"),
            fieldWithPath("releaseDate").type(STRING).description("????????????")
        );
    }

    private List<FieldDescriptor> page() {
        return List.of(
            fieldWithPath("page").type(NUMBER).description("?????????"),
            fieldWithPath("size").type(NUMBER).description("????????? ?????????"),
            fieldWithPath("totalPages").type(NUMBER).description("?????? ????????????"),
            fieldWithPath("totalElements").type(NUMBER).description("?????? ????????????"),
            fieldWithPath("sorted").type(BOOLEAN).description("?????? ??????"),
            fieldWithPath("isFirst").type(BOOLEAN).description("?????? ?????????"),
            fieldWithPath("isLast").type(BOOLEAN).description("????????? ?????????")
        );
    }

    private List<ParameterDescriptor> pageParam() {
        return List.of(
            parameterWithName("page").description("?????????").optional(),
            parameterWithName("size").description("????????? ?????????").optional(),
            parameterWithName("sortBy").description("?????? ??????").optional(),
            parameterWithName("direction").description("?????? ??????").optional()
        );
    }
}