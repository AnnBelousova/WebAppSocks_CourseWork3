package sock.shop.webapp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sock.shop.webapp.services.FileService;
import sock.shop.webapp.services.WarehouseService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Tag(name = "Файл контроллер", description = "Контроллер для работы с файлами: скачать/загрузить")
@RestController
@RequestMapping("/files")
public class FileController {
    private final FileService fileService;
    private final WarehouseService warehouseService;

    public FileController(FileService fileService, WarehouseService warehouseService) {
        this.fileService = fileService;
        this.warehouseService = warehouseService;
    }

    @GetMapping(value = "/export-file", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<InputStreamResource> downloadDataFie() throws FileNotFoundException {
        File file = fileService.getDataFile();
        if (file.exists()) {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .contentLength(file.length())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"SocksLog.json\"")
                    .body(resource);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(value = "/import-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadDataFile(@RequestParam MultipartFile file) {
        fileService.deleteDataFromFile();
        File dataFile = fileService.getDataFile();
        fileService.createOutputStream(dataFile, file);
    }
}
