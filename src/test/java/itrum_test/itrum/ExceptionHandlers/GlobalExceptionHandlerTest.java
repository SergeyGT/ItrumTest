package itrum_test.itrum.ExceptionHandlers;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import itrum_test.itrum.API.WalletController;
import itrum_test.itrum.DTOs.WalletOperationRequest;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFoundShouldReturn404WithErrorBody() {
        WalletNotFoundException exception = new WalletNotFoundException("Wallet not found");

        ResponseEntity<Map<String, String>> response = handler.handleNotFound(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("error", "Wallet not found");
    }

    @Test
    void handleInsufficientShouldReturn400WithErrorBody() {
        InsufficientFundsException exception = new InsufficientFundsException("Insufficient funds");

        ResponseEntity<Map<String, String>> response = handler.handleInsufficient(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "Insufficient funds");
    }

    @Test
    void handleValidationShouldReturn400WithGenericError() throws NoSuchMethodException {
        Method method = WalletController.class.getMethod("operate", WalletOperationRequest.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                new org.springframework.core.MethodParameter(method, 0),
                new BeanPropertyBindingResult(new Object(), "target"));

        ResponseEntity<Map<String, String>> response = handler.handleValidation(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "Invalid request");
    }

    @Test
    void handleInvalidJsonShouldReturn400WithInvalidJsonMessage() {
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("bad json", null, null);

        ResponseEntity<Map<String, String>> response = handler.handleInvalidJson(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "Invalid JSON");
    }
}
