package itrum_test.itrum.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import itrum_test.itrum.DTOs.CreateWalletRequest;
import itrum_test.itrum.DTOs.OperationType;
import itrum_test.itrum.DTOs.WalletOperationRequest;
import itrum_test.itrum.ExceptionHandlers.InsufficientFundsException;
import itrum_test.itrum.ExceptionHandlers.WalletNotFoundException;
import itrum_test.itrum.Model.Wallet;
import itrum_test.itrum.Repos.WalletRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository repository;
    private final EntityManager entityManager;

    @Transactional
    public Wallet createWallet(CreateWalletRequest request) {
        if (repository.existsById(request.getWalletId())) {
            return repository.findById(request.getWalletId()).orElseThrow(() ->
                    new WalletNotFoundException("Wallet not found: " + request.getWalletId()));
        }

        BigDecimal initialBalance = request.getInitialBalance() == null
                ? BigDecimal.ZERO
                : request.getInitialBalance();

        Wallet wallet = Wallet.builder()
                .id(request.getWalletId())
                .balance(initialBalance)
                .build();

        try {
            return repository.save(wallet);
        } catch (OptimisticLockingFailureException | DataIntegrityViolationException ex) {
            return repository.findById(request.getWalletId())
                    .orElseThrow(() -> ex);
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Wallet operate(WalletOperationRequest request) {
        Wallet wallet = entityManager.find(Wallet.class, request.getWalletId(), LockModeType.PESSIMISTIC_WRITE);
        if (wallet == null) {
            wallet = createMissingWallet(request.getWalletId());
        }

        if (request.getOperationType() == OperationType.DEPOSIT) {
            wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        } else {
            if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
                throw new InsufficientFundsException("Insufficient funds");
            }
            wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        }

        return repository.save(wallet);
    }

    private Wallet createMissingWallet(UUID walletId) {
        if (repository.existsById(walletId)) {
            return repository.findById(walletId)
                    .orElseThrow(() -> new WalletNotFoundException("Wallet not found: " + walletId));
        }

        Wallet wallet = Wallet.builder()
                .id(walletId)
                .balance(BigDecimal.ZERO)
                .build();

        try {
            return repository.save(wallet);
        } catch (OptimisticLockingFailureException | DataIntegrityViolationException ex) {
            return repository.findById(walletId)
                    .orElseThrow(() -> ex);
        }
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(UUID walletId) {
        return repository.findById(walletId)
                .map(Wallet::getBalance)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found: " + walletId));
    }
}