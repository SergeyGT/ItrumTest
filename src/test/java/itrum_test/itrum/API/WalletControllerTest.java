package itrum_test.itrum.API;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import itrum_test.itrum.DTOs.CreateWalletRequest;
import itrum_test.itrum.DTOs.WalletOperationRequest;
import itrum_test.itrum.Model.Wallet;
import itrum_test.itrum.Service.WalletService;

class WalletControllerTest {

    private final WalletService walletService = mock(WalletService.class);
    private final WalletController controller = new WalletController(walletService);

    @Test
    void createWalletShouldReturnCreatedResponse() {
        CreateWalletRequest request = new CreateWalletRequest();
        request.setWalletId(UUID.randomUUID());
        request.setInitialBalance(new BigDecimal("100.00"));

        Wallet wallet = Wallet.builder().id(request.getWalletId()).balance(request.getInitialBalance()).version(0L).build();
        when(walletService.createWallet(any(CreateWalletRequest.class))).thenReturn(wallet);

        ResponseEntity<Wallet> response = controller.createWallet(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(wallet);
        verify(walletService).createWallet(request);
    }

    @Test
    void operateShouldReturnOkResponse() {
        WalletOperationRequest request = new WalletOperationRequest();
        request.setWalletId(UUID.randomUUID());
        request.setAmount(new BigDecimal("10.00"));

        ResponseEntity<Void> response = controller.operate(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(walletService).operate(request);
    }

    @Test
    void getBalanceShouldReturnBalanceResponse() {
        UUID walletId = UUID.randomUUID();
        when(walletService.getBalance(walletId)).thenReturn(new BigDecimal("55.55"));

        ResponseEntity<BigDecimal> response = controller.getBalance(walletId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualByComparingTo("55.55");
        verify(walletService).getBalance(walletId);
    }
}
