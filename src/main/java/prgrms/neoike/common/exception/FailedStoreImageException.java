package prgrms.neoike.common.exception;

public class FailedStoreImageException extends RuntimeException {

    public FailedStoreImageException(String message) {
        super(message);
    }

    public FailedStoreImageException(String message, Throwable cause) {
        super(message, cause);
    }
}
