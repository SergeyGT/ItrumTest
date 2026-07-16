package itrum_test.itrum.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

import org.antlr.v4.runtime.misc.NotNull;

@Data
@NoArgsConstructor
public class WalletOperationRequest {

    @NotNull
    private UUID walletId;

    @NotNull
    private OperationType operationType;

    @NotNull
    private BigDecimal amount;
}