package itrum_test.itrum.DTOs;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class WalletOperationRequestTest {

    @Test
    void shouldCreateRequestWithNoArgsConstructor() {
        WalletOperationRequest request = new WalletOperationRequest();

        assertThat(request).isNotNull();
    }

    @Test
    void shouldSetAndGetWalletId() {
        WalletOperationRequest request = new WalletOperationRequest();
        UUID walletId = UUID.randomUUID();

        request.setWalletId(walletId);

        assertThat(request.getWalletId()).isEqualTo(walletId);
    }

    @Test
    void shouldSetAndGetOperationType() {
        WalletOperationRequest request = new WalletOperationRequest();

        request.setOperationType(OperationType.DEPOSIT);

        assertThat(request.getOperationType()).isEqualTo(OperationType.DEPOSIT);
    }

    @Test
    void shouldSetAndGetAmount() {
        WalletOperationRequest request = new WalletOperationRequest();
        BigDecimal amount = new BigDecimal("100.50");

        request.setAmount(amount);

        assertThat(request.getAmount()).isEqualByComparingTo(amount);
    }
}
