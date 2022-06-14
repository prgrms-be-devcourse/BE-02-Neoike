package prgrms.api;

import lombok.Getter;

@Getter
public enum ErrorCode {

    ENTITY_NOT_FOUND ("엔티티를 찾지 못하였습니다."),

    UNAUTHORIZED_ACCESS ("권한을 갖고 있지 않습니다."),

    SERVER_ERROR ("서버 에러가 발생하였습니다."),

    INVALID_INPUT_VALUE ("유효하지 않은 값입니다."),

    NOT_SUPPORTED_MEDIA_TYPE ("지원하지 않는 미디어 타입입니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
