package itrum_test.itrum.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import org.junit.jupiter.api.Test;

import itrum_test.itrum.DTOs.CreateWalletRequest;
import itrum_test.itrum.DTOs.OperationType;
import itrum_test.itrum.DTOs.WalletOperationRequest;
import itrum_test.itrum.Model.Wallet;
import itrum_test.itrum.Repos.WalletRepository;

class WalletServiceTest {

    @Test
    void createWalletShouldPersistNewWalletWithInitialBalance() {
        WalletRepository repository = mock(WalletRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        WalletService service = new WalletService(repository, entityManager);

        UUID walletId = UUID.randomUUID();
        CreateWalletRequest request = new CreateWalletRequest();
        request.setWalletId(walletId);
        request.setInitialBalance(new BigDecimal("100.50"));

        when(repository.existsById(walletId)).thenReturn(false);
        when(repository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet createdWallet = service.createWallet(request);

        assertThat(createdWallet.getId()).isEqualTo(walletId);
        assertThat(createdWallet.getBalance()).isEqualByComparingTo("100.50");
        verify(repository).save(any(Wallet.class));
    }

    @Test
    void operateShouldCreateWalletWhenMissingAndApplyDeposit() {
        WalletRepository repository = mock(WalletRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        WalletService service = new WalletService(repository, entityManager);

        UUID walletId = UUID.randomUUID();
        WalletOperationRequest request = new WalletOperationRequest();
        request.setWalletId(walletId);
        request.setOperationType(OperationType.DEPOSIT);
        request.setAmount(new BigDecimal("25.00"));

        when(entityManager.find(Wallet.class, walletId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(null);
        when(repository.existsById(walletId)).thenReturn(false);
        when(repository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet updatedWallet = service.operate(request);

        assertThat(updatedWallet.getId()).isEqualTo(walletId);
        assertThat(updatedWallet.getBalance()).isEqualByComparingTo("25.00");
        verify(repository, atLeastOnce()).save(any(Wallet.class));
    }
}
