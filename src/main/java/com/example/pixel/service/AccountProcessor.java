package com.example.pixel.service;

import com.example.pixel.exception.EntityNotFoundException;
import com.example.pixel.model.Account;
import com.example.pixel.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountProcessor {

    private final AccountRepository accountRepository;

    private static final BigDecimal MAX_MULTIPLIER = new BigDecimal("2.07");
    private static final BigDecimal INCREASE_PERCENT = new BigDecimal("0.10");

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOneAccount(Long accountId) {
        Account account = accountRepository.findByIdWithLock(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found: " + accountId));

        BigDecimal initial = account.getInitialBalance();
        BigDecimal maxBalance = initial.multiply(MAX_MULTIPLIER);
        BigDecimal newBalance = account.getCurrentBalance()
                .multiply(BigDecimal.ONE.add(INCREASE_PERCENT))
                .setScale(2, RoundingMode.HALF_UP);

        if (newBalance.compareTo(maxBalance) > 0) {
            newBalance = maxBalance;
        }

        account.setCurrentBalance(newBalance);
        accountRepository.save(account);
    }
}
