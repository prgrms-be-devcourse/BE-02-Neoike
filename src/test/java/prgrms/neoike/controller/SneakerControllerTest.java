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
    @DisplayName("신발 생성이 정상적으로 이루어진다.")
    void testRegisterNewSneaker() throws Exception {
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
                document("sneaker-register",
                    requestHeaders(commonHeaders()).and(host()),
                    requestFields(imagePaths())
                        .andWithPrefix(SNEAKER_PREFIX, commonSneaker())
                        .andWithPrefix(SNEAKER_STOCKS_PREFIX, sneakerStock()),
                    responseHeaders(commonHeaders()).and(location()),
                    responseFields(sneakerId(), sneakerCode())));
    }

    @Test
    @DisplayName("신발 아이디와 코드로 신발을 상세조회 할 수 있다.")
    void testGetSneakerDetail() throws Exception {
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
                document("sneaker-detail",
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
    @DisplayName("신발을 페이징 처리를 하여 전체조회 할 수 있다.")
    void testGetSneakers() throws Exception {
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
                document("sneaker-sneakers",
                    requestParameters(pageParam()),
                    responseHeaders(commonHeaders()),
                    responseFields(subsectionWithPath("contents").type(ARRAY).description("페이징 컨텐츠"))
                        .and(page())));
    }

    @Test
    @DisplayName("입력된 신발 재고의 아이디와 사이즈에 맞는 재고를 찾아서 입력된 수량만큼 감소시킨다.")
    void testDecreaseSneakerStock() throws Exception {
        SneakerStockRequest stockRequest = createTestStockRequest(250, 10);
        String requestString = objectMapper.writeValueAsString(stockRequest);
        SneakerStockResponse response = new SneakerStockResponse(1L, 250, 0, 2L);

        //given
        given(sneakerService.decreaseSneakerStock(any()))
            .willReturn(response);

        //when
        ResultActions resultActions = mockMvc
            .perform(
                put("/api/v1/sneakers/out/stocks/{stockId}", 1L)
                    .content(requestString)
                    .contentType(APPLICATION_JSON));

        //then
        resultActions
            .andExpectAll(
                status().isOk(),
                content().json(objectMapper.writeValueAsString(response)))
            .andDo(
                document("sneaker-stock_out",
                    requestHeaders(commonHeaders()).and(host()),
                    pathParameters(stockIdPath()),
                    requestFields(sneakerStock()),
                    responseHeaders(commonHeaders()),
                    responseFields(stockId()).and(sneakerStock()).and(sneakerId())));
    }

    @Test
    @DisplayName("입력된 신발 재고의 아이디와 사이즈에 맞는 재고를 찾아서 입력된 수량만큼 증가시킨다.")
    void testIncreaseSneakerStock() throws Exception {
        SneakerStockRequest stockRequest = createTestStockRequest(270, 100);
        String requestString = objectMapper.writeValueAsString(stockRequest);
        SneakerStockResponse response = new SneakerStockResponse(1L, 270, 110, 2L);

        //given
        given(sneakerService.increaseSneakerStock(any()))
            .willReturn(response);

        //when
        ResultActions resultActions = mockMvc
            .perform(
                put("/api/v1/sneakers/in/stocks/{stockId}", 1L)
                    .content(requestString)
                    .contentType(APPLICATION_JSON));

        //then
        resultActions
            .andExpectAll(
                status().isOk(),
                content().json(objectMapper.writeValueAsString(response)))
            .andDo(
                document("sneaker-stock_in",
                    requestHeaders(commonHeaders()).and(host()),
                    pathParameters(stockIdPath()),
                    requestFields(sneakerStock()),
                    responseHeaders(commonHeaders()),
                    responseFields(stockId()).and(sneakerStock()).and(sneakerId())));
    }

    private SneakerStockRequest createTestStockRequest(int size, int quantity) {
        return new SneakerStockRequest(size, quantity);
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
                createTestStockRequest(250, 10),
                createTestStockRequest(260, 10))
        );
    }

    private List<HeaderDescriptor> commonHeaders() {
        return List.of(
            headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입"),
            headerWithName(HttpHeaders.CONTENT_LENGTH).description("컨텐츠 길이")
        );
    }

    private HeaderDescriptor location() {
        return headerWithName(HttpHeaders.LOCATION).description("리다이렉트 주소");
    }

    private HeaderDescriptor host() {
        return headerWithName(HttpHeaders.HOST).description("호스트");
    }

    private FieldDescriptor imagePaths() {
        return fieldWithPath("imagePaths").type(ARRAY).description("신발 이미지 경로").optional();
    }

    private ParameterDescriptor codePath() {
        return parameterWithName("code").description("코드");
    }

    private ParameterDescriptor stockIdPath() {
        return parameterWithName("stockId").description("재고 아이디");
    }

    private ParameterDescriptor sneakerIdPath() {
        return parameterWithName("sneakerId").description("신발 아이디");
    }

    private FieldDescriptor sneakerId() {
        return fieldWithPath("sneakerId").type(NUMBER).description("신발 아이디");
    }

    private FieldDescriptor sneakerCode() {
        return fieldWithPath("code").type(STRING).description("코드");
    }

    private FieldDescriptor stockId() {
        return fieldWithPath("stockId").type(NUMBER).description("재고 아이디");
    }

    private List<FieldDescriptor> sneakerStock() {
        return List.of(
            fieldWithPath("size").type(NUMBER).description("재고 사이즈"),
            fieldWithPath("quantity").type(NUMBER).description("재고 수량")
        );
    }

    private List<FieldDescriptor> commonSneaker() {
        return List.of(
            fieldWithPath("memberCategory").type(STRING).description("멤버 카테고리"),
            fieldWithPath("sneakerCategory").type(STRING).description("신발 카테고리"),
            fieldWithPath("name").type(STRING).description("이름"),
            fieldWithPath("price").type(NUMBER).description("가격"),
            fieldWithPath("description").type(STRING).description("설명"),
            fieldWithPath("code").type(STRING).description("코드"),
            fieldWithPath("releaseDate").type(STRING).description("출시일자")
        );
    }

    private List<FieldDescriptor> page() {
        return List.of(
            fieldWithPath("page").type(NUMBER).description("페이지"),
            fieldWithPath("size").type(NUMBER).description("페이지 사이즈"),
            fieldWithPath("totalPages").type(NUMBER).description("전체 페이지수"),
            fieldWithPath("totalElements").type(NUMBER).description("전체 컨텐츠수"),
            fieldWithPath("sorted").type(BOOLEAN).description("정렬 상태"),
            fieldWithPath("isFirst").type(BOOLEAN).description("처음 페이지"),
            fieldWithPath("isLast").type(BOOLEAN).description("마지막 페이지")
        );
    }

    private List<ParameterDescriptor> pageParam() {
        return List.of(
            parameterWithName("page").description("페이지").optional(),
            parameterWithName("size").description("페이지 사이즈").optional(),
            parameterWithName("sortBy").description("정렬 기준").optional(),
            parameterWithName("direction").description("정렬 방향").optional()
        );
    }
}