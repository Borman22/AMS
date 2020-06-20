package tv.sonce.exceptions;

public class ClosingFileExceptionRT extends RuntimeException {

    public ClosingFileExceptionRT(String message) {
        super(message);
    }

    public ClosingFileExceptionRT(String message, Throwable cause) {
        super(message, cause);
    }

    public ClosingFileExceptionRT(Throwable cause) {
        super(cause);
    }
}
