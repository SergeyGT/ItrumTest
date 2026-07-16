package itrum_test.itrum.DTOs;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

import org.antlr.v4.runtime.misc.NotNull;

@Data
public class WalletOperationRequest {

    @NotNull
    private UUID walletId;

    @NotNull
    private OperationType operationType;

    @NotNull
    private BigDecimal amount;
}