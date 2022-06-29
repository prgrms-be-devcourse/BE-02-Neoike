package prgrms.neoike.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.QueryParameterException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import prgrms.neoike.common.api.ErrorResponse;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;
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

    //400 bad request
    @ExceptionHandler(
        value = {
            ConstraintViolationException.class,
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            QueryParameterException.class})
    protected ResponseEntity<ErrorResponse> handleBadRequests(final Exception ex) {
        log.warn("유효하지 않은 요청이 입력되었습니다. : {}", ex.getMessage(), ex);

        return ResponseEntity
            .badRequest()
            .body(ErrorResponse.of(INVALID_INPUT_VALUE, ex.getMessage()));
    }

    //415 unsupported Media Type
    @ExceptionHandler(HttpMediaTypeException.class)
    protected ResponseEntity<ErrorResponse> handleNotSupportedMediaType(final HttpMediaTypeException ex) {
        log.warn("지원하지 않는 미디어 타입입니다. : {}", ex.getMessage(), ex);

        return ResponseEntity
            .status(UNSUPPORTED_MEDIA_TYPE)
            .body(ErrorResponse.of(NOT_SUPPORTED_MEDIA_TYPE, ex.getMessage()));
    }

    @ExceptionHandler(InvalidDrawQuantityException.class)
    protected ResponseEntity<Object> handleInvalidDrawQuantityException (InvalidDrawQuantityException ex) {
        log.error("당첨에 실패하였습니다. {}", ex.getMessage());

        final ErrorResponse errorResponse = ErrorResponse.of(SERVER_ERROR, ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(final Exception ex) {
        log.error("예상치 못한 예외가 발생했습니다. {} ", ex.getMessage());

        return ResponseEntity
            .internalServerError()
            .body(ErrorResponse.of(SERVER_ERROR, ex.getMessage()));
    }
}