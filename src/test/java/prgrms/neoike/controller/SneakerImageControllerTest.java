package prgrms.neoike.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.test.web.servlet.ResultActions;
import prgrms.neoike.config.SecurityApiTest;
import prgrms.neoike.service.dto.sneakerimage.SneakerImageResponse;
import prgrms.neoike.service.image.SneakerImageService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SneakerImageController.class)
class SneakerImageControllerTest extends SecurityApiTest {

    @MockBean
    SneakerImageService sneakerImageService;

    @Test
    @DisplayName("신발 이미지 저장이 성공적으로 이루어진다.")
    void testUploadSneakerImages() throws Exception {
        MockMultipartFile testMultipartFile = getTestMultipartFile();
        SneakerImageResponse response = new SneakerImageResponse(List.of("/src/main/resources/sneaker/images/" + randomUUID() + ".PNG"));

        //given
        given(sneakerImageService.upload(any()))
            .willReturn(response);

        //when
        ResultActions resultActions = mockMvc
            .perform(
                multipart("/api/v1/images")
                    .file(testMultipartFile)
                    .contentType(MULTIPART_FORM_DATA));

        //then
        resultActions
            .andExpectAll(
                status().isOk(),
                content().json(objectMapper.writeValueAsString(response)))
            .andDo(document("image-upload",
                requestHeaders(contentType(), host()),
                requestPartBody("files"),
                responseHeaders(contentType()),
                responseFields(fieldWithPath("imagePaths").type(ARRAY).description("신발 이미지 경로"))));
    }

    private MockMultipartFile getTestMultipartFile() throws IOException {
        FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "/src/test/resources/test.PNG");

        return new MockMultipartFile("files", "test.PNG", IMAGE_PNG_VALUE, fis);
    }

    private HeaderDescriptor contentType() {
        return headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐츠 타입");
    }

    private HeaderDescriptor host() {
        return headerWithName(HttpHeaders.HOST).description("호스트");
    }
}