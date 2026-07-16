package itrum_test.itrum.ExceptionHandlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class InsufficientFundsExceptionTest {

    @Test
    void shouldThrowWithMessage() {
        String message = "Insufficient funds in wallet";

        assertThatThrownBy(() -> {
            throw new InsufficientFundsException(message);
        }).isInstanceOf(InsufficientFundsException.class)
                .hasMessage(message);
    }

    @Test
    void shouldExtendRuntimeException() {
        assertThat(new InsufficientFundsException("test")).isInstanceOf(RuntimeException.class);
    }
}
