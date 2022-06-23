package prgrms.neoike.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prgrms.neoike.controller.dto.sneaker.request.SneakerRegisterRequest;
import prgrms.neoike.service.SneakerService;
import prgrms.neoike.service.dto.sneaker.SneakerResponse;

import javax.validation.Valid;
import java.net.URI;

import static java.text.MessageFormat.format;
import static prgrms.neoike.controller.mapper.SneakerMapper.toDto;

@RestController
@RequestMapping("/api/v1/sneakers")
@RequiredArgsConstructor
public class SneakerController {

    private final SneakerService sneakerService;

    @PostMapping
    public ResponseEntity<SneakerResponse> registerSneaker(
        @RequestBody @Valid SneakerRegisterRequest registerRequest
    ) {
        SneakerResponse response = sneakerService.registerSneaker(toDto(registerRequest));

        return ResponseEntity
            .created(
                URI.create(
                    format("/api/v1/sneakers/{0}/{1}", response.sneakerId(), response.code())
                )
            )
            .body(response);
    }
}
