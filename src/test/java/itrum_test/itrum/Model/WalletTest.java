package itrum_test.itrum.Model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class WalletTest {

    @Test
    void shouldCreateWalletWithBuilder() {
        UUID id = UUID.randomUUID();
        BigDecimal balance = new BigDecimal("100.00");
        Long version = 1L;

        Wallet wallet = Wallet.builder()
                .id(id)
                .balance(balance)
                .version(version)
                .build();

        assertThat(wallet.getId()).isEqualTo(id);
        assertThat(wallet.getBalance()).isEqualByComparingTo(balance);
        assertThat(wallet.getVersion()).isEqualTo(version);
    }

    @Test
    void shouldSetAndGetBalance() {
        Wallet wallet = new Wallet();
        BigDecimal newBalance = new BigDecimal("250.50");

        wallet.setBalance(newBalance);

        assertThat(wallet.getBalance()).isEqualByComparingTo(newBalance);
    }

    @Test
    void shouldSetAndGetVersion() {
        Wallet wallet = new Wallet();
        Long newVersion = 5L;

        wallet.setVersion(newVersion);

        assertThat(wallet.getVersion()).isEqualTo(newVersion);
    }

    @Test
    void shouldSetAndGetId() {
        Wallet wallet = new Wallet();
        UUID id = UUID.randomUUID();

        wallet.setId(id);

        assertThat(wallet.getId()).isEqualTo(id);
    }

    @Test
    void shouldDefaultBalanceToZero() {
        Wallet wallet = new Wallet();

        assertThat(wallet.getBalance()).isEqualByComparingTo("0.00");
    }

    @Test
    void shouldCreateAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        BigDecimal balance = new BigDecimal("50.00");
        Long version = 2L;

        Wallet wallet = new Wallet(id, balance, version);

        assertThat(wallet.getId()).isEqualTo(id);
        assertThat(wallet.getBalance()).isEqualByComparingTo(balance);
        assertThat(wallet.getVersion()).isEqualTo(version);
    }
}
