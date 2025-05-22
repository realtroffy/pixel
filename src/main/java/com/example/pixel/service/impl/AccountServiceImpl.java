package com.example.pixel.service.impl;

import com.example.pixel.dto.TransferRequestDto;
import com.example.pixel.exception.EntityNotFoundException;
import com.example.pixel.exception.LockException;
import com.example.pixel.exception.ValidationException;
import com.example.pixel.model.Account;
import com.example.pixel.repository.AccountRepository;
import com.example.pixel.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public void transfer(Authentication authentication, TransferRequestDto transferRequestDto) {
        Long fromUserId = Long.valueOf(authentication.getName());

        if (fromUserId.equals(transferRequestDto.receiverId())) {
            throw new ValidationException("Cannot transfer money to yourself.");
        }

        if (transferRequestDto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Transfer amount must be greater than zero.");
        }

        Account from;
        Account to;

        try {
            from = tryLockAccount(fromUserId);
            to = tryLockAccount(transferRequestDto.receiverId());
        } catch (LockException e) {
            log.error("Transfer failed due to lock: {}", e.getMessage());
            throw new ValidationException("Transfer failed because one of the accounts is currently locked.");
        }

        if (from.getCurrentBalance().compareTo(transferRequestDto.amount()) < 0) {
            throw new ValidationException("Insufficient funds.");
        }

        from.setCurrentBalance(from.getCurrentBalance().subtract(transferRequestDto.amount()));
        to.setCurrentBalance(to.getCurrentBalance().add(transferRequestDto.amount()));

        log.info("Transferred {} from user {} to user {}",
                transferRequestDto.amount(), fromUserId, transferRequestDto.receiverId());
    }

    public Account tryLockAccount(Long userId) {
        try {
            return accountRepository.findWithDelayOneSecondByIdWithLock(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Account " + userId + " not found."));
        } catch (PessimisticLockingFailureException e) {
            log.error("Pessimistic lock failed for account {}: {}", userId, e.getMessage());
            throw new LockException("Account " + userId + " is locked.");
        }
    }
}