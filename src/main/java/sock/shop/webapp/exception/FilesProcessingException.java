package sock.shop.webapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FilesProcessingException extends RuntimeException {
    public FilesProcessingException() {
        super("Ошибки при работе с файлами");
    }

    public FilesProcessingException(String message) {
        super(message);
    }
}
