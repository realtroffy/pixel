package com.example.pixel.service;

import com.example.pixel.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountBatchProcessor {

    private final AccountProcessor accountProcessor;

    public void processBatch(List<Long> accountIds, Queue<Long> failedAccounts, int pageNum) {
        for (Long accountId : accountIds) {
            try {
                accountProcessor.processOneAccount(accountId);
            } catch (PessimisticLockingFailureException e) {
                log.error("Account {} is locked, skipping. Page: {}", accountId, pageNum);
                failedAccounts.add(accountId);
            } catch (Exception e) {
                log.error("Unexpected error while processing account {}: {}", accountId, e.getMessage());
                failedAccounts.add(accountId);
            }
        }
    }
}
