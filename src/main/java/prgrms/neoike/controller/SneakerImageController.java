package prgrms.neoike.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import prgrms.neoike.service.dto.sneakerimage.SneakerImageResponse;
import prgrms.neoike.service.dto.sneakerimage.SneakerImageUploadDto;
import prgrms.neoike.service.image.SneakerImageService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class SneakerImageController {

    private final SneakerImageService sneakerImageService;

    @PostMapping
    public ResponseEntity<SneakerImageResponse> uploadImages(
        List<MultipartFile> files
    ) {
        SneakerImageResponse imageIdResponses = sneakerImageService.upload(new SneakerImageUploadDto(files));

        return ResponseEntity.ok(imageIdResponses);
    }
}
