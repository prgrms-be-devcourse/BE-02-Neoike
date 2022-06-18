package prgrms.neoike.common.exception;

public class InvalidInputValueException extends RuntimeException {

    public InvalidInputValueException(String message) {
        super(message);
    }

    public InvalidInputValueException(String message, Throwable cause) {
        super(message, cause);
    }
}
