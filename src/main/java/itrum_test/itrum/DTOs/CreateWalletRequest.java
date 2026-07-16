package itrum_test.itrum.DTOs;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateWalletRequest {

    @NotNull
    private UUID walletId;

    private BigDecimal initialBalance = BigDecimal.ZERO;
}
