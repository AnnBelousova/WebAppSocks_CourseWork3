package sock.shop.webapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ExceptionThrowFilesProcessing extends RuntimeException {
    public ExceptionThrowFilesProcessing() {
        super("Ошибки при работе с файлами");
    }

    public ExceptionThrowFilesProcessing(String message) {
        super(message);
    }
}
