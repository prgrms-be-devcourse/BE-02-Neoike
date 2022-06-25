package prgrms.neoike.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import prgrms.neoike.controller.dto.sneaker.SneakerRegisterRequest;
import prgrms.neoike.controller.dto.sneaker.SneakerRequest;
import prgrms.neoike.controller.dto.sneaker.SneakerStockRequest;
import prgrms.neoike.service.SneakerService;
import prgrms.neoike.service.dto.page.PageResponse;
import prgrms.neoike.service.dto.sneaker.SneakerDetailResponse;
import prgrms.neoike.service.dto.sneaker.SneakerIdResponse;
import prgrms.neoike.service.dto.sneaker.SneakerResponse;
import prgrms.neoike.service.dto.sneaker.SneakerStockResponse;

import java.util.List;

import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static prgrms.neoike.domain.sneaker.MemberCategory.MEN;
import static prgrms.neoike.domain.sneaker.SneakerCategory.JORDAN;

@AutoConfigureRestDocs
@WebMvcTest(SneakerController.class)
class SneakerControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    SneakerService sneakerService;

    ObjectMapper objectMapper = new ObjectMapper()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .registerModule(new JavaTimeModule());

    @Test
    @DisplayName("신발 생성이 정상적으로 이루어진다.")
    void testRegisterNewSneaker() throws Exception {
        SneakerRegisterRequest registerRequest = createRegisterRequest();
        String requestJson = objectMapper.writeValueAsString(registerRequest);

        given(sneakerService.registerSneaker(any()))
            .willReturn(new SneakerIdResponse(1L, "DS-1234567"));

        MvcResult result = mvc.perform(
            post("/api/v1/sneakers")
                .content(requestJson)
                .contentType(APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andDo(
                document("sneaker-register",
                    requestFields(sneakerImage())
                        .andWithPrefix("sneaker.", sneaker())
                        .andWithPrefix("sneakerStocks[*].", sneakerStock()),
                    responseFields(commonPostMethod())
                )
            ).andReturn();

        String resultString = result.getResponse().getContentAsString();
        SneakerIdResponse response = objectMapper.readValue(resultString, SneakerIdResponse.class);

        assertThat(response).isNotNull();
        assertAll(
            () -> assertThat(response.sneakerId()).isEqualTo(1L),
            () -> assertThat(response.code()).isEqualTo("DS-1234567")
        );
    }

    @Test
    @DisplayName("신발 아이디와 코드로 신발을 상세조회 할 수 있다.")
    void testGetSneakerDetail() throws Exception {
        given(sneakerService.getSneakerDetail(any()))
            .willReturn(createDetailResponse());

        MvcResult result = mvc
            .perform(get("/api/v1/sneakers/{stockId}/{code}", 1, "DS-1234567"))
            .andExpect(status().isOk())
            .andDo(
                document("sneaker-detail",
                    pathParameters(commonPath()),
                    responseFields()
                        .andWithPrefix("sneakerStocks.[*].", sneakerStock())
                        .andWithPrefix("sneakerStocks.[*].", sneakerId(), stockId())
                        .andWithPrefix("sneaker.", sneaker())
                        .andWithPrefix("sneaker.", sneakerIdAndImages())
                )
            ).andReturn();

        String resultString = result.getResponse().getContentAsString();
        SneakerDetailResponse response = objectMapper.readValue(resultString, SneakerDetailResponse.class);

        assertThat(response).isNotNull();

        SneakerResponse sneakerResponse = response.sneaker();
        List<SneakerStockResponse> sneakerStockResponses = response.sneakerStocks();

        assertAll(
            () -> assertThat(sneakerResponse.code()).isEqualTo("DS-1234567"),
            () -> assertThat(sneakerResponse.imagePaths().size()).isOne(),
            () -> assertThat(sneakerResponse.price()).isNotNegative(),
            () -> assertThat(sneakerStockResponses).contains(new SneakerStockResponse(1L, 250, 10, 2L))
        );
    }

    @Test
    @DisplayName("신발을 페이징 처리를 하여 전체조회 할 수 있다.")
    void testGetSneakers() throws Exception {
        given(sneakerService.getSneakers(any()))
            .willReturn(createPageResponseWithSneakerResponse());

        MvcResult result = mvc
            .perform(
                get("/api/v1/sneakers")
                    .queryParam("page", "1")
                    .queryParam("size", "1")
                    .queryParam("sortBy", "createdAt.asc")
            )
            .andExpect(status().isOk())
            .andDo(
                document("sneaker-sneakers",
                    requestParameters(pageParam()),
                    responseFields(page())
                        .and(
                            subsectionWithPath("contents")
                            .type(ARRAY)
                            .description("페이징 컨텐츠")
                        )
                )
            ).andReturn();

        String resultString = result.getResponse().getContentAsString();
        PageResponse<SneakerResponse> pageResponse = objectMapper
            .readValue(resultString, new TypeReference<>() {});

        assertAll(
            () -> assertThat(pageResponse.page()).isEqualTo(1),
            () -> assertThat(pageResponse.isFirst()).isTrue(),
            () -> assertThat(pageResponse.sorted()).isTrue(),
            () -> assertThat(pageResponse.size()).isEqualTo(1)
        );

        List<SneakerResponse> contents = pageResponse.contents();

        assertThat(contents).isNotEmpty();
        assertAll(
            () -> assertThat(contents).hasSize(1),
            () -> assertThat(contents).containsExactly(createSneakerResponse()),
            () -> assertThat(contents.get(0)).isInstanceOf(SneakerResponse.class),
            () -> assertThat(contents.get(0).sneakerId()).isEqualTo(1)
        );
    }

    @Test
    @DisplayName("입력된 신발 재고의 아이디와 사이즈에 맞는 재고를 찾아서 입력된 수량만큼 감소시킨다.")
    void testDecreaseSneakerStock() throws Exception {
        SneakerStockRequest stockRequest = createTestStockRequest(250, 10);
        String requestString = objectMapper.writeValueAsString(stockRequest);

        given(sneakerService.decreaseSneakerStock(any()))
            .willReturn(new SneakerStockResponse(1L, 250, 0, 2L));

        MvcResult result = mvc
            .perform(
                put("/api/v1/sneakers/out/stocks/{stockId}", 1L)
                .content(requestString)
                .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andDo(
                document("sneaker-stock_out",
                    requestFields(sneakerStock()),
                    responseFields(sneakerStock())
                        .and(sneakerId())
                        .and(stockId())
                )
            ).andReturn();

        String resultString = result.getResponse().getContentAsString();
        SneakerStockResponse leftSneakerStockResponse = objectMapper.readValue(resultString, SneakerStockResponse.class);

        assertThat(leftSneakerStockResponse).isNotNull();
        assertAll(
            () -> assertThat(leftSneakerStockResponse.stockId()).isEqualTo(1),
            () -> assertThat(leftSneakerStockResponse.sneakerId()).isEqualTo(2),
            () -> assertThat(leftSneakerStockResponse.quantity()).isZero(),
            () -> assertThat(leftSneakerStockResponse.size()).isEqualTo(250)
        );
    }

    @Test
    @DisplayName("입력된 신발 재고의 아이디와 사이즈에 맞는 재고를 찾아서 입력된 수량만큼 증가시킨다.")
    void testIncreaseSneakerStock() throws Exception {
        SneakerStockRequest stockRequest = createTestStockRequest(270, 100);
        String requestString = objectMapper.writeValueAsString(stockRequest);

        given(sneakerService.increaseSneakerStock(any()))
            .willReturn(new SneakerStockResponse(1L, 270, 110, 2L));

        MvcResult result = mvc
            .perform(
                put("/api/v1/sneakers/in/stocks/{stockId}", 1L)
                    .content(requestString)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andDo(
                document("sneaker-stock_in",
                    requestFields(sneakerStock()),
                    responseFields(sneakerStock())
                        .and(stockId())
                        .and(sneakerId())
                )
            ).andReturn();

        String resultString = result.getResponse().getContentAsString();
        SneakerStockResponse leftSneakerStockResponse = objectMapper.readValue(resultString, SneakerStockResponse.class);

        assertThat(leftSneakerStockResponse).isNotNull();
        assertAll(
            () -> assertThat(leftSneakerStockResponse.stockId()).isEqualTo(1),
            () -> assertThat(leftSneakerStockResponse.sneakerId()).isEqualTo(2),
            () -> assertThat(leftSneakerStockResponse.quantity()).isEqualTo(110),
            () -> assertThat(leftSneakerStockResponse.size()).isEqualTo(270)
        );
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
            .releaseDate(of(2022, 10, 10, 10, 0, 0))
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
                .releaseDate(of(2022, 10, 10, 10, 0, 0))
                .build(),
            List.of(
                createTestStockRequest(250, 10),
                createTestStockRequest(260, 10))
        );
    }

    private List<FieldDescriptor> sneakerIdAndImages() {
        return List.of(
            fieldWithPath("sneakerId").type(NUMBER).description("신발 아이디"),
            sneakerImage()
        );
    }

    private List<FieldDescriptor> commonPostMethod() {
        return List.of(
            fieldWithPath("sneakerId").type(NUMBER).description("신발 아이디"),
            fieldWithPath("code").type(STRING).description("코드")
        );
    }

    private List<ParameterDescriptor> commonPath() {
        return List.of(
            parameterWithName("stockId").description("신발 재고 아이디"),
            parameterWithName("code").description("코드")
        );
    }

    private FieldDescriptor sneakerImage() {
        return fieldWithPath("imagePaths").type(ARRAY).description("신발 이미지 경로");
    }

    private List<FieldDescriptor> sneakerStock() {
        return List.of(
            fieldWithPath("size").type(NUMBER).description("사이즈"),
            fieldWithPath("quantity").type(NUMBER).description("수량")
        );
    }

    private FieldDescriptor stockId() {
        return fieldWithPath("stockId").type(NUMBER).description("신발 재고 아이디");
    }

    private FieldDescriptor sneakerId() {
        return fieldWithPath("sneakerId").type(NUMBER).description("신발 아이디");
    }

    private List<FieldDescriptor> sneaker() {
        return List.of(
            fieldWithPath("memberCategory").type(STRING).description("멤버 카테고리"),
            fieldWithPath("sneakerCategory").type(STRING).description("신발 카테고리"),
            fieldWithPath("name").type(STRING).description("이름"),
            fieldWithPath("price").type(NUMBER).description("가격"),
            fieldWithPath("description").type(STRING).description("상품설명"),
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
            fieldWithPath("sorted").type(BOOLEAN).description("정렬상태"),
            fieldWithPath("isFirst").type(BOOLEAN).description("첫 번째 페이지"),
            fieldWithPath("isLast").type(BOOLEAN).description("마지막 페이지")
        );
    }

    private List<ParameterDescriptor> pageParam() {
        return List.of(
            parameterWithName("page").description("페이지"),
            parameterWithName("size").description("페이지 사이즈"),
            parameterWithName("sortBy").description("페이지 정렬")
        );
    }
}