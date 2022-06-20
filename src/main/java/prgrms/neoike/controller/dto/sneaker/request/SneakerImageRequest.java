package prgrms.neoike.controller.dto.sneaker.request;

import javax.validation.constraints.NotBlank;

public record SneakerImageRequest(
    @NotBlank
    String originName,

    @NotBlank
    String encodedName
) {

}
