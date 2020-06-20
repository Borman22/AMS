package tv.sonce.exceptions;

public class TimeCodeFormatExceptionRT extends RuntimeException {

    public TimeCodeFormatExceptionRT(String message) {
        super(message);
    }

    public TimeCodeFormatExceptionRT(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeCodeFormatExceptionRT(Throwable cause) {
        super(cause);
    }
}
