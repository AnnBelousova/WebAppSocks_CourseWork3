package sock.shop.webapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sock.shop.webapp.model.Color;
import sock.shop.webapp.model.Size;
import sock.shop.webapp.model.Sock;
import sock.shop.webapp.services.WarehouseService;

import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/warehouse")
@Tag(name = "Склад", description = "Контроллер для работы со складом")
public class WarehouseController {
    private final WarehouseService warehouseService;

    @PostMapping
    @Operation(
            summary = "Добавление новых носков на склад",
            description = "Пользователь должен заполнть все поля"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Носки добавлены на склад",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Sock.class))}

            )})
    public ResponseEntity<Sock> addSock(@Valid @RequestBody Sock sock) {
        warehouseService.addSocks(sock);
        return ResponseEntity.ok(sock);
    }

    @PutMapping
    @Operation(
            summary = "Добавление количества носков на склад",
            description = "Пользователь должен заполнть все обязательные поля"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Такая пара носок найдена",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Sock.class))}

            )})
    public ResponseEntity<Void> updateSock(@RequestParam Color color, @RequestParam Size size, @RequestParam int cottonPart, @RequestParam int quantity) {
        warehouseService.updateSocks(color, size, cottonPart, quantity);
        return ResponseEntity.ok().build();
    }


    @GetMapping()
    @Operation(
            summary = "Получение всего списка носков",
            description = "Будет выведеен список всех носков из базы данных"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Носки найдены",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Sock.class))}
            )})
    public Collection<Sock> getSocks() {
        return this.warehouseService.getSocks();

    }

    @GetMapping("/socks-quantity")
    @Operation(
            summary = "Получение количества пар носков по цвету и min/max количества хлопка в составе.",
            description = "Пользователь должен ввести цвет, min, max количество хлопка в состваве"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Носки с такими параметрами посчитаны"
            )
    })
    public ResponseEntity<Integer> getSocksByColorCottonPartMinMax(@RequestParam Color color, @RequestParam int min, @RequestParam int max) {
        return ResponseEntity.ok(warehouseService.getQuantityByColorByCottonPartMin(color, min, max));
    }

    @PutMapping("/transfer")
    @Operation(
            summary = "Выдача носков со склада",
            description = "Пользователь должен ввести цвет, min, max количество хлопка в состваве"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Носки выданы"
            )
    })
    public ResponseEntity<Void> transferSocksFromWarehouse(@RequestParam Color color, @RequestParam Size size,
                                                           @RequestParam int cottonPart,
                                                           @RequestParam int quantity) {
        warehouseService.transferSockFromWarehouse(color, size, cottonPart, quantity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/recycling")
    @Operation(
            summary = "Удаление носков со склада",
            description = "Пользователь должен ввести цвет, min, max количество хлопка в состваве"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Носки удалены"
            )
    })
    public ResponseEntity<Void> deleteSocksFromWarehouse(@RequestParam Color color, @RequestParam Size size,
                                                         @RequestParam int cottonPart,
                                                         @RequestParam int quantity) {
        warehouseService.transferSockFromWarehouse(color, size, cottonPart, quantity);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/export-report")
    public ResponseEntity<Object> getSocksList() {
        try {
            Path path = warehouseService.createListOfSocksWithTransactions();
            if (Files.size(path) == 0) {
                return ResponseEntity.noContent().build();
            }
            InputStreamResource resource = new InputStreamResource(new FileInputStream(path.toFile()));
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .contentLength(Files.size(path))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Report.txt\"")
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
