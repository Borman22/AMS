package tv.sonce.exceptions;

public class FileNotFoundExceptionRT extends RuntimeException {

    public FileNotFoundExceptionRT(String message) {
        super(message);
    }

    public FileNotFoundExceptionRT(String message, Throwable cause) {
        super(message, cause);
    }

    public FileNotFoundExceptionRT(Throwable cause) {
        super(cause);
    }

}
