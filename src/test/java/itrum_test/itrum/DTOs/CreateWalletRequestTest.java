package itrum_test.itrum.DTOs;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class CreateWalletRequestTest {

    @Test
    void shouldCreateRequestWithNoArgsConstructor() {
        CreateWalletRequest request = new CreateWalletRequest();

        assertThat(request).isNotNull();
    }

    @Test
    void shouldSetAndGetWalletId() {
        CreateWalletRequest request = new CreateWalletRequest();
        UUID walletId = UUID.randomUUID();

        request.setWalletId(walletId);

        assertThat(request.getWalletId()).isEqualTo(walletId);
    }

    @Test
    void shouldSetAndGetInitialBalance() {
        CreateWalletRequest request = new CreateWalletRequest();
        BigDecimal initialBalance = new BigDecimal("250.75");

        request.setInitialBalance(initialBalance);

        assertThat(request.getInitialBalance()).isEqualByComparingTo(initialBalance);
    }

    @Test
    void shouldDefaultInitialBalanceToZero() {
        CreateWalletRequest request = new CreateWalletRequest();

        assertThat(request.getInitialBalance()).isEqualByComparingTo("0.00");
    }
}
