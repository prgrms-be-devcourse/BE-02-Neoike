package prgrms.neoike.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import prgrms.neoike.controller.dto.sneaker.SneakerRegisterRequest;
import prgrms.neoike.controller.dto.sneaker.SneakerStockRequest;
import prgrms.neoike.service.SneakerService;
import prgrms.neoike.service.dto.page.PageResponse;
import prgrms.neoike.service.dto.sneaker.SneakerDetailResponse;
import prgrms.neoike.service.dto.sneaker.SneakerIdResponse;
import prgrms.neoike.service.dto.sneaker.SneakerResponse;
import prgrms.neoike.service.dto.sneaker.SneakerStockResponse;

import javax.validation.Valid;
import java.net.URI;

import static java.text.MessageFormat.format;
import static prgrms.neoike.controller.mapper.SneakerMapper.*;

@Validated
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
        @RequestParam(defaultValue = "1") String page,
        @RequestParam(defaultValue = "20") String size,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "desc") String direction
    ) {
        PageResponse<SneakerResponse> sneakers = sneakerService.getSneakers(toPagingDto(page, size, sortBy, direction));

        return ResponseEntity.ok(sneakers);
    }

    @PutMapping("/out/stocks/{stockId}")
    public ResponseEntity<SneakerStockResponse> decreaseSneakerStock(
        @PathVariable Long stockId,
        @RequestBody @Valid SneakerStockRequest stockRequest
    ) {
        SneakerStockResponse sneakerStockResponse = sneakerService
            .decreaseSneakerStock(toSneakerStockUpdateDto(stockId, stockRequest));

        return ResponseEntity.ok(sneakerStockResponse);
    }

    @PutMapping("/in/stocks/{stockId}")
    public ResponseEntity<SneakerStockResponse> increaseSneakerStock(
        @PathVariable Long stockId,
        @RequestBody @Valid SneakerStockRequest stockRequest
    ) {
        SneakerStockResponse sneakerStockResponse = sneakerService
            .increaseSneakerStock(toSneakerStockUpdateDto(stockId, stockRequest));

        return ResponseEntity.ok(sneakerStockResponse);
    }
}
