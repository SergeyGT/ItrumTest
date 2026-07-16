package itrum_test.itrum.DTOs;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class OperationTypeTest {

    @Test
    void shouldHaveDepositAndWithdrawValues() {
        assertThat(OperationType.DEPOSIT).isNotNull();
        assertThat(OperationType.WITHDRAW).isNotNull();
    }

    @Test
    void shouldValueOf() {
        OperationType deposit = OperationType.valueOf("DEPOSIT");
        OperationType withdraw = OperationType.valueOf("WITHDRAW");

        assertThat(deposit).isEqualTo(OperationType.DEPOSIT);
        assertThat(withdraw).isEqualTo(OperationType.WITHDRAW);
    }

    @Test
    void shouldEnumValues() {
        OperationType[] values = OperationType.values();

        assertThat(values).hasSize(2);
        assertThat(values).contains(OperationType.DEPOSIT, OperationType.WITHDRAW);
    }
}
