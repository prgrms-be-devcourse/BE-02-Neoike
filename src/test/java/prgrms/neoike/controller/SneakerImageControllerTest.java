package prgrms.neoike.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import prgrms.neoike.config.SecurityApiTest;
import prgrms.neoike.service.dto.sneakerimage.SneakerImageResponse;
import prgrms.neoike.service.image.SneakerImageService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SneakerImageController.class)
class SneakerImageControllerTest extends SecurityApiTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    SneakerImageService sneakerImageService;


    @Test
    @DisplayName("신발 이미지 저장이 성공적으로 이루어진다.")
    void testUploadSneakerImages() throws Exception {
        MockMultipartFile testMultipartFile = getTestMultipartFile();
        SneakerImageResponse expectedResponse = new SneakerImageResponse(List.of("/src/main/resources/sneaker/images/" + now() + ".PNG"));

        given(sneakerImageService.upload(any()))
            .willReturn(expectedResponse);

        MvcResult result = mvc.perform(multipart("/api/v1/images")
            .file(testMultipartFile)
            .contentType(MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andDo(document("image-upload",
                responseFields(
                    fieldWithPath("imagePaths").type(ARRAY).description("신발 이미지 경로")
                ))
            ).andReturn();

        String resultString = result.getResponse().getContentAsString();
        SneakerImageResponse response = objectMapper.readValue(resultString, SneakerImageResponse.class);


        assertThat(response).isNotNull();
        assertAll(
            () -> assertThat(response.imagePaths().size()).isOne(),
            () -> assertThat(response.imagePaths().get(0)).isEqualTo(expectedResponse.imagePaths().get(0))
        );
    }

    private MockMultipartFile getTestMultipartFile() throws IOException {
        FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "/src/test/resources/test.PNG");

        return new MockMultipartFile("test", "test.PNG", IMAGE_PNG_VALUE, fis);
    }
}