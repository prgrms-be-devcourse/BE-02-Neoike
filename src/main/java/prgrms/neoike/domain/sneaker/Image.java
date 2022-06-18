package prgrms.neoike.domain.sneaker;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class Image {

    @NotBlank
    @Column(name = "dir", nullable = false)
    String dir;

    @NotBlank
    @Column(name = "origin_name", nullable = false)
    String originName;

    @NotBlank
    @Column(name = "stored_name", nullable = false)
    String storedName;

    public Image(String dir, String originName, String storedName) {
        this.dir = dir;
        this.originName = originName;
        this.storedName = storedName;
    }
}
