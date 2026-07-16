package itrum_test.itrum.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import itrum_test.itrum.DTOs.CreateWalletRequest;
import itrum_test.itrum.DTOs.OperationType;
import itrum_test.itrum.DTOs.WalletOperationRequest;
import itrum_test.itrum.ExceptionHandlers.InsufficientFundsException;
import itrum_test.itrum.ExceptionHandlers.WalletNotFoundException;
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

    @Test
    void operateShouldWithdrawWhenBalanceIsSufficient() {
        WalletRepository repository = mock(WalletRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        WalletService service = new WalletService(repository, entityManager);

        UUID walletId = UUID.randomUUID();
        Wallet wallet = Wallet.builder().id(walletId).balance(new BigDecimal("100.00")).version(1L).build();
        WalletOperationRequest request = new WalletOperationRequest();
        request.setWalletId(walletId);
        request.setOperationType(OperationType.WITHDRAW);
        request.setAmount(new BigDecimal("30.00"));

        when(entityManager.find(Wallet.class, walletId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(wallet);
        when(repository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet updatedWallet = service.operate(request);

        assertThat(updatedWallet.getBalance()).isEqualByComparingTo("70.00");
    }

    @Test
    void operateShouldThrowWhenBalanceIsInsufficient() {
        WalletRepository repository = mock(WalletRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        WalletService service = new WalletService(repository, entityManager);

        UUID walletId = UUID.randomUUID();
        Wallet wallet = Wallet.builder().id(walletId).balance(new BigDecimal("10.00")).version(1L).build();
        WalletOperationRequest request = new WalletOperationRequest();
        request.setWalletId(walletId);
        request.setOperationType(OperationType.WITHDRAW);
        request.setAmount(new BigDecimal("20.00"));

        when(entityManager.find(Wallet.class, walletId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(wallet);

        assertThatThrownBy(() -> service.operate(request))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessage("Insufficient funds");
    }

    @Test
    void getBalanceShouldReturnStoredBalance() {
        WalletRepository repository = mock(WalletRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        WalletService service = new WalletService(repository, entityManager);

        UUID walletId = UUID.randomUUID();
        Wallet wallet = Wallet.builder().id(walletId).balance(new BigDecimal("42.50")).version(1L).build();

        when(repository.findById(walletId)).thenReturn(Optional.of(wallet));

        BigDecimal balance = service.getBalance(walletId);

        assertThat(balance).isEqualByComparingTo("42.50");
    }

    @Test
    void getBalanceShouldThrowWhenWalletMissing() {
        WalletRepository repository = mock(WalletRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        WalletService service = new WalletService(repository, entityManager);

        UUID walletId = UUID.randomUUID();

        when(repository.findById(walletId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getBalance(walletId))
                .isInstanceOf(WalletNotFoundException.class)
                .hasMessage("Wallet not found: " + walletId);
    }

    @Test
    void createWalletShouldReturnExistingWalletWhenAlreadyExists() {
        WalletRepository repository = mock(WalletRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        WalletService service = new WalletService(repository, entityManager);

        UUID walletId = UUID.randomUUID();
        Wallet existingWallet = Wallet.builder().id(walletId).balance(new BigDecimal("50.00")).version(1L).build();
        CreateWalletRequest request = new CreateWalletRequest();
        request.setWalletId(walletId);
        request.setInitialBalance(new BigDecimal("100.00"));

        when(repository.existsById(walletId)).thenReturn(true);
        when(repository.findById(walletId)).thenReturn(Optional.of(existingWallet));

        Wallet result = service.createWallet(request);

        assertThat(result.getId()).isEqualTo(walletId);
        assertThat(result.getBalance()).isEqualByComparingTo("50.00");
    }

    @Test
    void createWalletShouldUseZeroBalanceWhenInitialBalanceIsNull() {
        WalletRepository repository = mock(WalletRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        WalletService service = new WalletService(repository, entityManager);

        UUID walletId = UUID.randomUUID();
        CreateWalletRequest request = new CreateWalletRequest();
        request.setWalletId(walletId);
        request.setInitialBalance(null);

        when(repository.existsById(walletId)).thenReturn(false);
        when(repository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = service.createWallet(request);

        assertThat(result.getBalance()).isEqualByComparingTo("0.00");
    }

    @Test
    void createWalletShouldHandleDataIntegrityViolationOnConcurrentCreate() {
        WalletRepository repository = mock(WalletRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        WalletService service = new WalletService(repository, entityManager);

        UUID walletId = UUID.randomUUID();
        Wallet savedWallet = Wallet.builder().id(walletId).balance(BigDecimal.ZERO).version(0L).build();
        CreateWalletRequest request = new CreateWalletRequest();
        request.setWalletId(walletId);
        request.setInitialBalance(BigDecimal.ZERO);

        when(repository.existsById(walletId)).thenReturn(false);
        when(repository.save(any(Wallet.class))).thenThrow(new DataIntegrityViolationException("duplicate"));
        when(repository.findById(walletId)).thenReturn(Optional.of(savedWallet));

        Wallet result = service.createWallet(request);

        assertThat(result.getId()).isEqualTo(walletId);
    }

    @Test
    void operateShouldApplyDepositToExistingWallet() {
        WalletRepository repository = mock(WalletRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        WalletService service = new WalletService(repository, entityManager);

        UUID walletId = UUID.randomUUID();
        Wallet wallet = Wallet.builder().id(walletId).balance(new BigDecimal("100.00")).version(1L).build();
        WalletOperationRequest request = new WalletOperationRequest();
        request.setWalletId(walletId);
        request.setOperationType(OperationType.DEPOSIT);
        request.setAmount(new BigDecimal("50.00"));

        when(entityManager.find(Wallet.class, walletId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(wallet);
        when(repository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = service.operate(request);

        assertThat(result.getBalance()).isEqualByComparingTo("150.00");
    }

    @Test
    void operateShouldWithdrawExactAmount() {
        WalletRepository repository = mock(WalletRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        WalletService service = new WalletService(repository, entityManager);

        UUID walletId = UUID.randomUUID();
        Wallet wallet = Wallet.builder().id(walletId).balance(new BigDecimal("100.00")).version(1L).build();
        WalletOperationRequest request = new WalletOperationRequest();
        request.setWalletId(walletId);
        request.setOperationType(OperationType.WITHDRAW);
        request.setAmount(new BigDecimal("100.00"));

        when(entityManager.find(Wallet.class, walletId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(wallet);
        when(repository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = service.operate(request);

        assertThat(result.getBalance()).isEqualByComparingTo("0.00");
    }

    @Test
    void operateShouldCreateWalletWhenMissingAndApplyWithdrawThrowsInsufficientFunds() {
        WalletRepository repository = mock(WalletRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        WalletService service = new WalletService(repository, entityManager);

        UUID walletId = UUID.randomUUID();
        WalletOperationRequest request = new WalletOperationRequest();
        request.setWalletId(walletId);
        request.setOperationType(OperationType.WITHDRAW);
        request.setAmount(new BigDecimal("50.00"));

        when(entityManager.find(Wallet.class, walletId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(null);
        when(repository.existsById(walletId)).thenReturn(false);
        when(repository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThatThrownBy(() -> service.operate(request))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessage("Insufficient funds");
    }
    
    @Test
    void operateShouldAutoCreateWalletWhenMissingDuringDeposit() {
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

        Wallet result = service.operate(request);

        assertThat(result.getId()).isEqualTo(walletId);
        assertThat(result.getBalance()).isEqualByComparingTo("25.00");
    }
}
