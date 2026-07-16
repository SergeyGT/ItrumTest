package itrum_test.itrum.API;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import itrum_test.itrum.DTOs.CreateWalletRequest;
import itrum_test.itrum.DTOs.WalletOperationRequest;
import itrum_test.itrum.Model.Wallet;
import jakarta.validation.Valid;
import itrum_test.itrum.Service.WalletService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping({"/wallet/create", "/wallets/create"})
    public ResponseEntity<Wallet> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        Wallet wallet = walletService.createWallet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(wallet);
    }

    @PostMapping({"/wallet", "/wallets"})
    public ResponseEntity<Void> operate(@Valid @RequestBody WalletOperationRequest request) {
        walletService.operate(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping({"/wallets/{walletId}", "/wallet/{walletId}"})
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID walletId) {
        BigDecimal balance = walletService.getBalance(walletId);
        return ResponseEntity.ok(balance);
    }
}