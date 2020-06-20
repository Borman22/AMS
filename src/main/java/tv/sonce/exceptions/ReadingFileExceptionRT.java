package tv.sonce.exceptions;

public class ReadingFileExceptionRT extends RuntimeException {

    public ReadingFileExceptionRT(String message) {
        super(message);
    }

    public ReadingFileExceptionRT(String message, Throwable cause) {
        super(message, cause);
    }

    public ReadingFileExceptionRT(Throwable cause) {
        super(cause);
    }

}
