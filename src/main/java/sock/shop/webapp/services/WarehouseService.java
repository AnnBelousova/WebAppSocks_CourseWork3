package sock.shop.webapp.services;

import org.springframework.stereotype.Service;
import sock.shop.webapp.model.Color;
import sock.shop.webapp.model.Size;
import sock.shop.webapp.model.Sock;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

@Service
public interface WarehouseService {
    Sock addSocks(Sock sock);

    void transferSockFromWarehouse(Sock sock);

    int getQuantityByColorByCottonPartMin(Color color, Size size, int cottonPartMin, int cottonPartMax);

    Path createListOfSocksWithTransactions() throws IOException;
}
