package sock.shop.webapp.services.impl;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sock.shop.webapp.exception.FilesProcessingException;
import sock.shop.webapp.services.FileService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


@Service
public class FileServiceImpl implements FileService {
    @Value("${pathToDataFile}")
    private String dataFilePath;
    @Value("${nameOfDataSocks}")
    private String dataFileName;


    @Override
    public boolean saveSockToFile(String json) {
        Path path = Path.of(dataFilePath, dataFileName);
        try {
            deleteDataFromFile();
            Files.writeString(path, json);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String readFromFile() {
        Path path = Path.of(dataFilePath, dataFileName);
        try {
            return Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FilesProcessingException("Файл не прочитан");
        }
    }


    @Override
    public File getDataFile() {
        return new File(dataFilePath + "/" + dataFileName);
    }

    @Override
    public boolean isFileExists() {
        Path path = Path.of(dataFilePath, dataFileName);
        File file = new File(path.toUri());
        return file.exists() && !file.isDirectory();
    }

    @Override
    public boolean deleteDataFromFile() {
        Path path = Path.of(dataFilePath, dataFileName);
        try {
            Files.deleteIfExists(path);
            Files.createFile(path);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ResponseEntity<Object> createOutputStream(File dataFile, MultipartFile file) {
        try (
                FileOutputStream fos = new FileOutputStream(dataFile)) {
            IOUtils.copy(file.getInputStream(), fos);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
            throw new FilesProcessingException("Файл не сохранен");
        }
    }

    @Override
    public void createFile() {
        Path path = Path.of(dataFilePath, dataFileName);
        try {
            Files.createFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Path createTempFile(String suffix) {
        try {
            return Files.createTempFile(Path.of(dataFilePath), "tempFile", suffix);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
