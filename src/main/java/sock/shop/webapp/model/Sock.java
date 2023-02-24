package sock.shop.webapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.List;

@Data
@EqualsAndHashCode(of = {"color", "size", "cottonPart"})
@AllArgsConstructor
@NoArgsConstructor
public class Sock {
    private Color color;
    private Size size;

    @Min(1)
    @Max(100)
    @NotNull(message = "Процент хлопка должен быть от 0% до 100%")
    private int cottonPart;
    @PositiveOrZero
    private int quantity;
    private List<Transaction> transactions;
}
