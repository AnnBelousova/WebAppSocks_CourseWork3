package sock.shop.webapp.services;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;

public interface FileService {
    boolean saveSockToFile(String json);

    String readFromFile();

    File getDataFile();

    boolean isFileExists();

    boolean deleteDataFromFile();

    ResponseEntity<Object> createOutputStream(File dataFile, MultipartFile file);

    void createFile();

    Path createTempFile(String suffix);
}
