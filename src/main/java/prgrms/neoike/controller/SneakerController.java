package prgrms.neoike.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prgrms.neoike.controller.dto.sneaker.SneakerRegisterRequest;
import prgrms.neoike.service.SneakerService;
import prgrms.neoike.service.dto.page.PageResponse;
import prgrms.neoike.service.dto.sneaker.SneakerDetailResponse;
import prgrms.neoike.service.dto.sneaker.SneakerIdResponse;
import prgrms.neoike.service.dto.sneaker.SneakerResponse;

import javax.validation.Valid;
import java.net.URI;

import static java.text.MessageFormat.format;
import static prgrms.neoike.controller.mapper.SneakerMapper.*;

@RestController
@RequestMapping("/api/v1/sneakers")
@RequiredArgsConstructor
public class SneakerController {

    private final SneakerService sneakerService;

    @PostMapping
    public ResponseEntity<SneakerIdResponse> registerSneaker(
        @RequestBody @Valid SneakerRegisterRequest registerRequest
    ) {
        SneakerIdResponse response = sneakerService.registerSneaker(toSneakerRegisterDto(registerRequest));

        return ResponseEntity
            .created(
                URI.create(
                    format("/api/v1/sneakers/{0}/{1}", response.sneakerId(), response.code())
                )
            )
            .body(response);
    }

    @GetMapping("/{sneakerId}/{code}")
    public ResponseEntity<SneakerDetailResponse> getSneakerDetail(
        @PathVariable Long sneakerId,
        @PathVariable String code
    ) {
        SneakerDetailResponse sneakerDetail = sneakerService.getSneakerDetail(toSneakerDetailDto(sneakerId, code));

        return ResponseEntity.ok(sneakerDetail);
    }

    @GetMapping
    public ResponseEntity<PageResponse<SneakerResponse>> getSneakers(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "createdAt.desc") String sortBy
    ) {
        PageResponse<SneakerResponse> sneakers = sneakerService.getSneakers(toPagingDto(page, size, sortBy));

        return ResponseEntity.ok(sneakers);
    }
}
