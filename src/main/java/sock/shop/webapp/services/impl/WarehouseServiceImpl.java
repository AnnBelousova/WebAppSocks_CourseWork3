package sock.shop.webapp.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import sock.shop.webapp.model.*;
import sock.shop.webapp.services.FileService;
import sock.shop.webapp.services.WarehouseService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private static long id = 0;
    private static Map<Long, Sock> socksMap = new HashMap<>();
    private final FileService fileService;

    List<Transaction> transactions = new ArrayList<>();

    @PostConstruct
    private void init() {
        try {
            if (fileService.isFileExists() == true) {
                readData();
            } else
                fileService.createFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Sock addSocks(Sock sock) {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), Type.IN, sock.getQuantity()));
        Sock sockAdd = new Sock(sock.getColor(), sock.getSize(), sock.getCottonPart(), sock.getQuantity(), transactions);
        socksMap.put(id++, sockAdd);
        saveData();
        return sock;
    }

    @Override
    public void updateSocks(Color color, Size size, int cottonPart, int quantity) {
        for (Map.Entry<Long, Sock> s : socksMap.entrySet()) {
            if (s.getValue().getColor().equals(color)
                    && s.getValue().getSize().equals(size)
                    && s.getValue().getCottonPart() == cottonPart) {
                int qtn = s.getValue().getQuantity() + quantity;
                List<Transaction> transactions = s.getValue().getTransactions();
                transactions.add(new Transaction(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), Type.IN, quantity));
                long key = s.getKey();
                socksMap.put(key, new Sock(color, size, cottonPart, qtn, transactions));
                saveData();
            } else {
                throw new NotFoundException("Таких носков нет на складе");
            }
        }
    }


    @Override
    public Collection<Sock> getSocks() {
        return socksMap.values();
    }

    @Override
    public void transferSockFromWarehouse(Color color, Size size, int cottonPart, int quantity) {
        for (Map.Entry<Long, Sock> s : socksMap.entrySet()) {
            if (s.getValue().getColor().equals(color)
                    && s.getValue().getSize().equals(size)
                    && s.getValue().getCottonPart() == cottonPart
                    && s.getValue().getQuantity() >= quantity) {
                int qtn = s.getValue().getQuantity() - quantity;
                List<Transaction> transactions = s.getValue().getTransactions();
                transactions.add(new Transaction(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), Type.OUT, quantity));
                long key = s.getKey();
                socksMap.put(key, new Sock(color, size, cottonPart, qtn, transactions));
                saveData();
            } else {
                throw new NotFoundException("Таких носков нет на складе");
            }
        }
    }


    @Override
    public int getQuantityByColorByCottonPartMin(Color color, int cottonPartMin, int cottonPartMax) {
        int count = 0;
        List<Sock> sockList = socksMap.entrySet()
                .stream()
                .filter(sock ->
                        (sock.getValue()
                                .getColor()
                                .equals(color) &&
                                sock.getValue().getCottonPart() >= cottonPartMin &&
                                sock.getValue().getCottonPart() <= cottonPartMax))
                .map((Map.Entry::getValue))
                .collect(Collectors.toList());
        for (int i = 0; i < sockList.size(); i++) {
            count += sockList.get(i).getQuantity();
        }
        return count;
    }

    private void saveData() {
        try {
            String json = new ObjectMapper().writeValueAsString(socksMap);
            fileService.saveSockToFile(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void readData() {
        String json = fileService.readFromFile();
        try {
            socksMap = new ObjectMapper().readValue(json, new TypeReference<HashMap<Long, Sock>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path createListOfSocksWithTransactions() throws IOException {
        Map<Long, Sock> sockHashMap = socksMap;
        Path path = fileService.createTempFile("report");

        try (Writer writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
            for (Sock sock : sockHashMap.values()) {
                writer.append("Цвет: " + sock.getColor() + "\n"
                        + "Размер: " + sock.getSize() + "\n"
                        + "Количество хлопка: " + sock.getCottonPart() + "\n"
                        + "Количество на складе: " + sock.getQuantity() + "\n"
                        + "Транзакции: \n");

                for (int i = 0; i < sock.getTransactions().size(); i++) {
                    String text = "получено на склад: ";
                    if (sock.getTransactions().get(i).getType().equals(Type.IN)) {
                        writer.append(i + 1 + ") ");
                        writer.append(text);
                        writer.append(sock.getTransactions().get(i).getQuantity() + "\n"
                                + "время: " + sock.getTransactions().get(i).getDateTime() + "\n");
                    } else if (sock.getTransactions().get(i).getType().equals(Type.OUT)) {
                        text = "выдано со склада: ";
                        writer.append(i + 1 + ") ");
                        writer.append(text + " ");
                        writer.append(sock.getTransactions().get(i).getQuantity() + "\n"
                                + "время: " + sock.getTransactions().get(i).getDateTime() + "\n");
                    }
                }
                writer.append("---------------------------\n");
            }
        }
        return path;
    }

}
