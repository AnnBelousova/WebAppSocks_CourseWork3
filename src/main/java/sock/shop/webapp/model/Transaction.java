package sock.shop.webapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private String dateTime;
    private Type type;
    int quantity;
}
