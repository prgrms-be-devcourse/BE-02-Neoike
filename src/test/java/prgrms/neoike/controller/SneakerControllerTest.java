package prgrms.neoike.controller;

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
import prgrms.neoike.controller.dto.sneaker.request.SneakerRegisterRequest;
import prgrms.neoike.controller.dto.sneaker.request.SneakerRequest;
import prgrms.neoike.controller.dto.sneaker.request.SneakerStockRequest;
import prgrms.neoike.service.SneakerService;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
                        .andWithPrefix("sneakerStocks.", sneakerStock()),
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
            .perform(get("/api/v1/sneakers/{sneakerId}/{code}", 1, "DS-1234567"))
            .andExpect(status().isOk())
            .andDo(
                document("sneaker-detail",
                    pathParameters(commonParam()),
                    responseFields()
                        .andWithPrefix("sneakerStocks.", sneakerStock())
                        .andWithPrefix("sneaker.", sneaker())
                        .andWithPrefix("sneaker.", commonGetMethod())
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
            () -> assertThat(sneakerStockResponses).contains(new SneakerStockResponse(250, 10))
        );
    }

    private SneakerDetailResponse createDetailResponse() {
        return new SneakerDetailResponse(
            List.of(
                new SneakerStockResponse(250, 10),
                new SneakerStockResponse(260, 10)),
            SneakerResponse
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
                .build()
        );
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
                new SneakerStockRequest(250, 10),
                new SneakerStockRequest(260, 10))
        );
    }

    private List<FieldDescriptor> commonGetMethod() {
        return List.of(
            fieldWithPath("sneakerId").type(NUMBER).description("신발 아이디"),
            sneakerImage()
        );
    }

    private FieldDescriptor sneakerImage() {
        return fieldWithPath("imagePaths").type(ARRAY).description("신발 이미지 경로");
    }

    private List<FieldDescriptor> sneakerStock() {
        return List.of(
            fieldWithPath("[*].size").type(NUMBER).description("사이즈"),
            fieldWithPath("[*].quantity").type(NUMBER).description("수량")
        );
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

    private List<FieldDescriptor> commonPostMethod() {
        return List.of(
            fieldWithPath("sneakerId").type(NUMBER).description("신발 아이디"),
            fieldWithPath("code").type(STRING).description("코드")
        );
    }

    private List<ParameterDescriptor> commonParam() {
        return List.of(
            parameterWithName("sneakerId").description("신발 아이디"),
            parameterWithName("code").description("코드")
        );
    }
}