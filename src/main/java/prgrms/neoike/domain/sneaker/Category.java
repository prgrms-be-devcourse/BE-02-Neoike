package prgrms.neoike.domain.sneaker;

import lombok.Getter;

@Getter
public enum Category {

    LIFESTYLE("라이프스타일"),
    RUNNING("러닝"),
    JORDAN("조던"),
    BASKETBALL("농구"),
    SOCCER("축구"),
    SANDAL("샌들 & 슬리퍼");

    private final String description;

    Category(String description) {
        this.description = description;
    }
}