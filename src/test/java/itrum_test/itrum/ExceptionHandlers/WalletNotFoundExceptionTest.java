package itrum_test.itrum.ExceptionHandlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class WalletNotFoundExceptionTest {

    @Test
    void shouldThrowWithMessage() {
        String message = "Wallet not found: 123";

        assertThatThrownBy(() -> {
            throw new WalletNotFoundException(message);
        }).isInstanceOf(WalletNotFoundException.class)
                .hasMessage(message);
    }

    @Test
    void shouldExtendRuntimeException() {
        assertThat(new WalletNotFoundException("test")).isInstanceOf(RuntimeException.class);
    }
}
