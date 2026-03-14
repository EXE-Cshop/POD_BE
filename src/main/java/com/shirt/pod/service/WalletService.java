package com.shirt.pod.service;

import com.shirt.pod.model.entity.Wallet;
import com.shirt.pod.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

    private static final BigDecimal POINTS_PER_REMIX_SALE = BigDecimal.TEN;

    private final WalletRepository walletRepository;

    @Transactional
    public Wallet addPoints(Long userId, BigDecimal points) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    log.info("Creating new wallet for userId={}", userId);
                    return Wallet.builder()
                            .userId(userId)
                            .balance(BigDecimal.ZERO)
                            .build();
                });

        wallet.setBalance(wallet.getBalance().add(points));
        Wallet saved = walletRepository.save(wallet);
        log.info("Added {} points to wallet of userId={}. New balance={}", points, userId, saved.getBalance());
        return saved;
    }

    @Transactional(readOnly = true)
    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> Wallet.builder()
                        .userId(userId)
                        .balance(BigDecimal.ZERO)
                        .build());
    }

    public BigDecimal getPointsPerRemixSale() {
        return POINTS_PER_REMIX_SALE;
    }
}
