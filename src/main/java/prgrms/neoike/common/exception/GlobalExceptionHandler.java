package prgrms.neoike.common.exception;

import javax.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import prgrms.neoike.common.api.ErrorResponse;

import static prgrms.neoike.common.api.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
        log.error("엔티티를 찾을 수 없습니다. {}", ex.getMessage());

        final ErrorResponse errorResponse = ErrorResponse.of(ENTITY_NOT_FOUND, ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(InvalidDrawQuantityException.class)
    protected ResponseEntity<Object> handleInvalidDrawQuantityException (InvalidDrawQuantityException ex) {
        log.error("당첨에 실패하였습니다. {}", ex.getMessage());

        final ErrorResponse errorResponse = ErrorResponse.of(SERVER_ERROR, ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleInvalidInputValueException (IllegalArgumentException ex) {
        log.error("입력값이 유효하지 않습니다. {}", ex.getMessage());

        final ErrorResponse errorResponse = ErrorResponse.of(INVALID_INPUT_VALUE, ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(final Exception ex) {
        log.error("예상치 못한 예외가 발생했습니다. {} ", ex.getMessage());

        final ErrorResponse errorResponse = ErrorResponse.of(SERVER_ERROR, ex.getMessage());
        return ResponseEntity.internalServerError().body(errorResponse);
    }
}